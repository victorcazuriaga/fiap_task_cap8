# Tarefa Fase 3
**Capitulo 8:  Microsserviços com Spring - Rest com Spring Boot**

# Atenção — Atividade Avaliativa

> **Importante:**
>
> * Verifique se o arquivo do upload está correto: **não é possível enviar outro arquivo após o fechamento da entrega na plataforma ou após a correção do professor**.
> * **Não deixe para os últimos minutos** do prazo — problemas de conexão ou sistema podem impedir a entrega. As entregas são realizadas **apenas pela plataforma**.
> * **Não disponibilize a resposta** da sua atividade em grupos (WhatsApp, Discord, Teams etc.). Isso pode gerar plágio e zerar a atividade para todos.
> * Você tem **15 dias** após a publicação da nota para solicitar revisão da correção.

---

# Desafio — Serviço RESTful com foco em ESG

Neste desafio, você e seu grupo irão desenvolver um **serviço RESTful** com **Java + Spring Boot**, aplicando os conceitos de ESG — especificamente o tema **Governança e Compliance Ambiental (Tema 4)**.

O objetivo é criar um conjunto de endpoints e funcionalidades que auxiliem empresas a **registrar, monitorar e garantir a conformidade com normas ambientais**, além de automatizar processos de auditoria e emissão de alertas para licenças e compensações de carbono.

---

## Objetivo do Projeto

* Implementar **mínimo 4 endpoints RESTful** relevantes ao tema ESG 4 (Governança e Compliance Ambiental);
* Integrar a aplicação ao **banco de dados Oracle**;
* Utilizar **migrações de banco** (Flyway ou Liquibase);
* Aplicar **Spring Security** para proteger endpoints sensíveis;
* Containerizar o projeto com **Docker**;
* Entregar o código e collection do **Postman/Insomnia**.

---

## Endpoints sugeridos (tema 4 — Governança e Compliance Ambiental)

* `POST /licencas-ambientais` — Registrar nova licença ambiental.
* `GET /licencas-ambientais` — Consultar licenças existentes e status de validade.
* `PUT /licencas-ambientais/{id}` — Atualizar informações de uma licença.
* `GET /emissoes-carbono` — Monitorar emissões e conformidade com limites legais.
* `POST /compensacoes-carbono` — Cadastrar compensações ambientais (ex.: reflorestamento).
* `GET /auditorias` — Listar auditorias ambientais registradas.
* `POST /alertas-renovacao` — Criar alertas automáticos para renovação de licenças.

> **Exemplo de fluxo:** o sistema registra automaticamente conformidades, gera relatórios de auditoria e envia alertas quando licenças ou compensações ambientais precisam ser renovadas.

---

## Requisitos técnicos

1. **Java + Spring Boot**;
2. **Banco Oracle** com gerenciamento de migrações (Flyway/Liquibase);
3. **Spring Security** (autenticação JWT ou básica);
4. **Docker** e configuração pronta para execução (`Dockerfile` e opcional `docker-compose.yml`);
5. **Collection do Postman/Insomnia** documentando todos os endpoints;
6. **README** com instruções de execução e exemplos de requests/responses.

---

## Estrutura recomendada do repositório

```
├── README.md
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── src/
│   ├── main/java/...
│   └── main/resources/
│       ├── application.yml
│       └── db/migration/
└── collection/
```

---

## Exemplo de `.env.example`

```
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@//oracle:1521/ORCLPDB1
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=senha
SPRING_PROFILES_ACTIVE=dev
JWT_SECRET=segredoSeguro
```

---

## Entrega

* `project.zip` — código-fonte completo com Docker e migrações.
* `collection.zip` — collection do Postman/Insomnia.

---


**Tema escolhido:** Governança e Compliance Ambiental
**Foco:** Registro e monitoramento de conformidade ambiental, auditorias e alertas de licenças.
