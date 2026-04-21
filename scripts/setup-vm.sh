#!/usr/bin/env bash
# Bootstrap da VM Ubuntu 22.04 que hospeda staging+prod em containers.
# Executar UMA VEZ, como o usuário sudo da VM (ex: azureuser), após provisionar.
# Uso: bash setup-vm.sh

set -euo pipefail

echo ">>> 1/5 Atualizando pacotes..."
sudo apt-get update -y
sudo apt-get install -y ca-certificates curl gnupg lsb-release jq

echo ">>> 2/5 Instalando Docker Engine + Compose plugin..."
if ! command -v docker >/dev/null 2>&1; then
    sudo install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" \
        | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update -y
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    sudo usermod -aG docker "$USER"
fi

echo ">>> 3/5 Instalando Azure CLI (para az acr login via managed identity)..."
if ! command -v az >/dev/null 2>&1; then
    curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
fi

echo ">>> 4/5 Criando diretórios de deploy..."
sudo mkdir -p /opt/compliance/staging /opt/compliance/prod
sudo chown -R "$USER":"$USER" /opt/compliance

echo ">>> 5/5 Instalando script de deploy (/usr/local/bin/deploy.sh)..."
sudo tee /usr/local/bin/deploy.sh > /dev/null <<'DEPLOY'
#!/usr/bin/env bash
# Uso: deploy.sh <staging|prod> <image_tag>
# Ex.:  deploy.sh staging staging-abc1234
set -euo pipefail

ENV="${1:?uso: deploy.sh <staging|prod> <image_tag>}"
TAG="${2:?uso: deploy.sh <staging|prod> <image_tag>}"
ACR="fiapdevops"
ACR_LOGIN="fiapdevops.azurecr.io"

case "$ENV" in staging|prod) ;; *) echo "env invalido"; exit 1 ;; esac

DIR="/opt/compliance/$ENV"
cd "$DIR"

# 1) Login no ACR via managed identity da VM
az acr login --name "$ACR" --identity >/dev/null

# 2) Atualizar APP_IMAGE no .env
NEW_IMAGE="$ACR_LOGIN/compliance:$TAG"
if grep -q '^APP_IMAGE=' .env; then
    sed -i "s|^APP_IMAGE=.*|APP_IMAGE=$NEW_IMAGE|" .env
else
    echo "APP_IMAGE=$NEW_IMAGE" >> .env
fi

# 3) Pull + recreate
export COMPOSE_PROJECT_NAME="compliance-$ENV"
docker compose pull app
docker compose up -d --no-deps --wait app

# 4) Limpar imagens antigas
docker image prune -f >/dev/null

echo "deploy $ENV -> $NEW_IMAGE OK"
DEPLOY
sudo chmod +x /usr/local/bin/deploy.sh

echo ""
echo "========================================================================"
echo "VM pronta. Próximos passos (manuais, uma vez):"
echo ""
echo "  1. Criar /opt/compliance/staging/.env a partir do template .env.staging.example"
echo "  2. Criar /opt/compliance/prod/.env    a partir do template .env.prod.example"
echo "  3. Copiar deploy/docker-compose.yaml para ambos os diretórios"
echo "  4. Primeiro boot:"
echo "       cd /opt/compliance/staging && COMPOSE_PROJECT_NAME=compliance-staging docker compose up -d"
echo "       cd /opt/compliance/prod    && COMPOSE_PROJECT_NAME=compliance-prod    docker compose up -d"
echo ""
echo "  Importante: faça logout/login pra aplicar o grupo docker no usuário."
echo "========================================================================"
