# Projeto - Cidades ESG Inteligentes (Compliance Ambiental)

Serviço RESTful em **Java + Spring Boot** focado no pilar **Governança e Compliance Ambiental (ESG - Tema 4)**, preparado para um ciclo completo de DevOps: containerização, orquestração, pipeline de CI/CD e deploy automatizado em **staging** e **produção** na Azure.

---

## Como executar localmente com Docker

### Pré-requisitos
- Docker e Docker Compose v2
- (Opcional) Java 17 e Maven para rodar fora de container

### Passos

```bash
# 1. Clone o repositório
git clone <url-do-repo>
cd fiap_task_cap8

# 2. (Opcional) Ajuste variáveis criando um .env a partir do template
cp .env.example .env

# 3. Suba app + PostgreSQL + RabbitMQ
docker compose up --build -d

# 4. Acompanhe os logs da aplicação
docker compose logs -f app
```

Quando o container `compliance-app` reportar `Started ComplianceApplication`, a API estará disponível em:

- API: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>
- Health check: <http://localhost:8080/actuator/health>
- RabbitMQ UI: <http://localhost:15672> (guest / guest)

### Endpoints principais

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Cadastro de cliente | público |
| POST | `/api/auth/login` | Login → retorna JWT | público |
| GET | `/api/client` | Lista (paginada) | `ROLE_ADMIN` |
| GET | `/api/client/{id}` | Busca por ID | autenticado |
| GET | `/api/client/cnpj/{cnpj}` | Busca por CNPJ | autenticado |
| PUT | `/api/client/{id}` | Atualiza | autenticado |
| DELETE | `/api/client/{id}` | Remove | `ROLE_ADMIN` |

### Rodando sem Docker

```bash
./mvnw -Dspring.profiles.active=dev spring-boot:run
```

### Rodando os testes

```bash
./mvnw test -Dspring.profiles.active=test
```

Os testes usam **H2 em memória em modo PostgreSQL**, sem necessidade de banco externo.

---

## Pipeline CI/CD

Todo o ciclo é automatizado via **GitHub Actions**. Três workflows:

### 1. [`.github/workflows/ci.yml`](.github/workflows/ci.yml) — em todo push/PR para `main` e `development`

| Etapa | Ação |
|---|---|
| Checkout | `actions/checkout@v4` |
| JDK | `actions/setup-java@v4` (Temurin 17) com cache Maven |
| Compile | `./mvnw clean compile` |
| **Testes** | `./mvnw test -Dspring.profiles.active=test` |
| Artefatos | Sobe relatórios Surefire + JaCoCo + JAR (retenção 7 dias) |
| Docker check | `docker/build-push-action` sem push — valida que a imagem builda |

### 2. [`.github/workflows/deploy-staging.yml`](.github/workflows/deploy-staging.yml) — push em `development` ou `workflow_dispatch`

| Job | Descrição |
|---|---|
| `test` | Roda a mesma bateria de testes (gate de qualidade) |
| `build-and-push` | Builda imagem Docker e publica no **GHCR** com tags `staging` e `staging-<sha>` |
| `deploy` | Login Azure (SP) → `azure/webapps-deploy@v3` atualiza **Azure Web App for Containers** (staging) → polling de `/actuator/health` |

### 3. [`.github/workflows/deploy-prod.yml`](.github/workflows/deploy-prod.yml) — tags `v*.*.*` ou `workflow_dispatch`

Mesmo fluxo do staging, mas com **environment protegido** (`production`) — exige aprovação manual configurada no GitHub antes de publicar. Imagem promovida com tag `prod`.

### Segredos/Vars necessários no GitHub

| Nome | Tipo | Uso |
|---|---|---|
| `AZURE_CREDENTIALS` | **Secret** | Service Principal em JSON (`az ad sp create-for-rbac --sdk-auth`) |
| `AZURE_WEBAPP_NAME_STAGING` | **Variable** | Nome do Web App de staging |
| `AZURE_WEBAPP_NAME_PROD` | **Variable** | Nome do Web App de produção |

### Fluxo resumido

```
push development  ─►  CI (build + test)          ─►  deploy-staging  ─►  Azure Web App (staging)
tag v1.2.3        ─►  CI (build + test)          ─►  deploy-prod (approval) ─►  Azure Web App (prod)
```

---

## Containerização

### Dockerfile (multi-stage, não-root, healthcheck)

```dockerfile
# Stage 1 — build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests && mv target/*.jar target/app.jar

# Stage 2 — runtime (JRE slim, user não-root, healthcheck)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN groupadd --system --gid 1001 spring \
 && useradd  --system --uid 1001 --gid spring --shell /sbin/nologin spring \
 && apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=builder --chown=spring:spring /app/target/app.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod SERVER_PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
USER spring
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -fsS http://localhost:${SERVER_PORT}/actuator/health/liveness || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
```

### Estratégias adotadas

| Prática | Como |
|---|---|
| **Multi-stage** | Etapa 1 com `maven:3.9.6-eclipse-temurin-17` só para empacotar; etapa 2 apenas com `eclipse-temurin:17-jre-jammy` (menor e sem Maven) |
| **Cache de dependências** | `dependency:go-offline` antes do `COPY src` — rebuild fica ~10x mais rápido |
| **Usuário não-root** | `spring:1001` — reduz blast radius em caso de RCE |
| **Heap dinâmico** | `-XX:MaxRAMPercentage=75.0` — ajusta ao limite do container |
| **Healthcheck** | `curl` em `/actuator/health/liveness` — orquestradores descobrem container doente |
| **Secrets fora da imagem** | Nenhuma credencial no build; tudo via env vars em runtime |

### Orquestração (docker-compose)

Dois cenários, um único template:

- [`docker-compose.yaml`](docker-compose.yaml) — **dev local**: `app` (com `build: .`) + `postgres:16-alpine` + `rabbitmq:3-management`, rede `compliance-net`, volume `postgres-data`, healthchecks, `depends_on: service_healthy`.
- [`deploy/docker-compose.yaml`](deploy/docker-compose.yaml) — **VM Azure (staging+prod)**: mesma stack, mas `app` usa `image: ${APP_IMAGE}` (pulled do ACR, sem build). Os dois ambientes compartilham esse arquivo — isolamento via `COMPOSE_PROJECT_NAME=compliance-staging` vs `compliance-prod` e `.env` por diretório.

### Infra na Azure — 1 VM com docker-compose

Escolha pragmática pra tarefa acadêmica: **1 VM Linux `B1s` hospeda staging e prod lado a lado** — staging em `:8080`, prod em `:8081`. Tudo rodando em container (PostgreSQL + RabbitMQ + App), com imagens puxadas do ACR via **Managed Identity**.

**Custo**: ~$7.59/mês a VM (ou **zero** dentro do crédito Azure for Students). ACR `Standard` já provisionado.

**Recursos já existentes:**
- ACR: `fiapdevops` (`fiapdevops.azurecr.io`)
- Resource Group: `fase-devops`
- Subscription: `d997ddf3-ed22-4adf-8419-0b589792cc97`
- Região: `brazilsouth`

#### 1. Provisionar a VM (Azure Cloud Shell, rodar uma vez)

```bash
# === variáveis ===
SUBSCRIPTION=d997ddf3-ed22-4adf-8419-0b589792cc97
RG=fase-devops
LOCATION=brazilsouth
ACR=fiapdevops
VM=compliance-vm
ADMIN_USER=victorcazuriaga

az account set --subscription $SUBSCRIPTION

# === criar VM Ubuntu 22.04 B1s com SSH key gerada ===
az vm create -g $RG -n $VM \
  --image Ubuntu2204 \
  --size Standard_B1s \
  --admin-username $ADMIN_USER \
  --generate-ssh-keys \
  --public-ip-sku Standard \
  --assign-identity                          # system-assigned Managed Identity

# === abrir portas 22 (SSH), 8080 (staging), 8081 (prod) ===
az vm open-port -g $RG -n $VM --port 22   --priority 1000
az vm open-port -g $RG -n $VM --port 8080 --priority 1001
az vm open-port -g $RG -n $VM --port 8081 --priority 1002

# === dar AcrPull à Managed Identity da VM ===
PRINCIPAL_ID=$(az vm identity show -g $RG -n $VM --query principalId -o tsv)
ACR_ID=$(az acr show -n $ACR --query id -o tsv)
az role assignment create --assignee $PRINCIPAL_ID --role AcrPull --scope $ACR_ID

VM_IP=$(az vm show -d -g $RG -n $VM --query publicIps -o tsv)
echo "VM pronta em $VM_IP"
```

#### 2. Bootstrap dentro da VM (instala docker, az-cli e o script de deploy)

```bash
# Na sua máquina, copiar o script e rodar
scp scripts/setup-vm.sh $ADMIN_USER@$VM_IP:~/
ssh $ADMIN_USER@$VM_IP 'bash setup-vm.sh'
# Logout e login novamente para o grupo docker valer
ssh $ADMIN_USER@$VM_IP 'newgrp docker'
```

O [`scripts/setup-vm.sh`](scripts/setup-vm.sh) instala Docker + Compose plugin + Azure CLI, cria `/opt/compliance/{staging,prod}`, e deixa pronto `/usr/local/bin/deploy.sh`.

#### 3. Primeiro boot (uma vez por ambiente)

```bash
# Copiar o compose file e o template de env pra dentro da VM
scp deploy/docker-compose.yaml   $ADMIN_USER@$VM_IP:/opt/compliance/staging/docker-compose.yaml
scp deploy/docker-compose.yaml   $ADMIN_USER@$VM_IP:/opt/compliance/prod/docker-compose.yaml
scp deploy/.env.staging.example  $ADMIN_USER@$VM_IP:/opt/compliance/staging/.env
scp deploy/.env.prod.example     $ADMIN_USER@$VM_IP:/opt/compliance/prod/.env

# Na VM: editar os .env (trocar POSTGRES_PASSWORD e JWT_SECRET de verdade!)
ssh $ADMIN_USER@$VM_IP 'nano /opt/compliance/staging/.env'
ssh $ADMIN_USER@$VM_IP 'nano /opt/compliance/prod/.env'

# Subir os dois stacks — login no ACR usa a Managed Identity automaticamente
ssh $ADMIN_USER@$VM_IP 'az login --identity && az acr login -n fiapdevops'
ssh $ADMIN_USER@$VM_IP 'cd /opt/compliance/staging && COMPOSE_PROJECT_NAME=compliance-staging docker compose up -d'
ssh $ADMIN_USER@$VM_IP 'cd /opt/compliance/prod    && COMPOSE_PROJECT_NAME=compliance-prod    docker compose up -d'
```

- Staging: <http://VM_IP:8080/actuator/health>, <http://VM_IP:8080/swagger-ui.html>
- Prod: <http://VM_IP:8081/actuator/health>

#### 4. Service Principal + chaves SSH pro GitHub Actions

```bash
# Service Principal com permissões mínimas (ACR push + login)
SP_JSON=$(az ad sp create-for-rbac \
  --name "gh-actions-compliance" \
  --role AcrPush \
  --scopes $ACR_ID \
  --sdk-auth)
echo "$SP_JSON"   # → cole em AZURE_CREDENTIALS

# Chave SSH pro pipeline entrar na VM (gerar par dedicado é mais seguro que reusar)
ssh-keygen -t ed25519 -f gh-deploy-key -N ""
cat gh-deploy-key.pub | ssh $ADMIN_USER@$VM_IP 'cat >> ~/.ssh/authorized_keys'
cat gh-deploy-key      # → cole em VM_SSH_PRIVATE_KEY
```

#### 5. Secrets e Variables necessários no GitHub

| Nome | Tipo | Valor |
|---|---|---|
| `AZURE_CREDENTIALS` | **Secret** | JSON do Service Principal (só precisa de `AcrPush`) |
| `VM_SSH_PRIVATE_KEY` | **Secret** | conteúdo completo do `gh-deploy-key` (chave privada ed25519) |
| `VM_HOST` | **Variable** | IP público da VM |
| `VM_USER` | **Variable** | `victorcazuriaga` |

> ACR (`fiapdevops`), nomes de recursos e portas já estão no topo dos workflows.

---

## Prints do funcionamento

> Substitua os placeholders abaixo pelas imagens reais após rodar o pipeline.

- **Pipeline CI (build + testes)**: `docs/prints/ci-pipeline.png`
- **Deploy staging concluído**: `docs/prints/deploy-staging.png`
- **Deploy produção concluído**: `docs/prints/deploy-prod.png`
- **Staging respondendo**: `docs/prints/staging-healthcheck.png`
- **Produção respondendo**: `docs/prints/prod-healthcheck.png`

---

## Tecnologias utilizadas

| Camada | Stack |
|---|---|
| Linguagem / runtime | Java 17 (Eclipse Temurin) |
| Framework | Spring Boot 3.5.7 (Web, Security, Data JPA, AMQP, Actuator, Validation) |
| Auth | Spring Security + JWT (`com.auth0:java-jwt` 4.4.0) |
| Persistência | PostgreSQL 16 + Hibernate + **Flyway** (migrations versionadas) |
| Mensageria | RabbitMQ 3 (spring-boot-starter-amqp) |
| Resiliência | Resilience4j (Spring Cloud) |
| Testes | JUnit 5, Mockito, Spring MockMvc, H2 (perfil `test`), JaCoCo |
| Build | Maven 3.9 (multi-stage Dockerfile) |
| Container | Docker, Docker Compose v2 |
| Registro de imagem | GitHub Container Registry (GHCR) |
| CI/CD | GitHub Actions |
| Cloud | **Azure Web App for Containers** + **Azure Database for PostgreSQL Flexible Server** |
| Observabilidade | Spring Boot Actuator (`/health`, `/info`, `/metrics`, `/prometheus`) |

---

## Checklist de entrega

| Item | OK |
|---|---|
| Projeto compactado em `.zip` com estrutura organizada | ☐ |
| Dockerfile funcional | ☑ |
| `docker-compose.yml` (dev + overlays staging/prod) | ☑ |
| Pipeline com etapas de build, teste e deploy | ☑ |
| README.md com instruções e prints | ☑ (prints pendentes após deploy) |
| Documentação técnica com evidências (PDF ou PPT) | ☐ |
| Deploy realizado nos ambientes staging e produção | ☐ (após configurar Azure) |
