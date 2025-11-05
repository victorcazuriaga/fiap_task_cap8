# Documentação rápida dos Endpoints (API)

> Observação: o projeto contém uma alteração na migration V1 para incluir `licenca_ambiental`. Se essa migration já foi aplicada no banco, prefira criar uma nova migration TIPO V2 em vez de editar V1. Ja deixei os SLQ pontos tambem

---

## Endpoints criados

Resumo das rotas principais criadas:

- Client (já existente no projeto)
  - GET /api/client
  - GET /api/client/{id}
  - GET /api/client/cnpj/{cnpj}
  - GET /api/client/profile (cabeçalho Authorization: Bearer <token>)
  - PUT /api/client/{id}
  - DELETE /api/client/{id}

- Licença Ambiental
  - POST /api/licenca
  - GET  /api/licenca/{id}
  - GET  /api/licenca/cliente/{clienteId}
  - PUT  /api/licenca/{id}
  - DELETE /api/licenca/{id}

- Resíduo
  - POST /api/residuo
  - GET  /api/residuo
  - GET  /api/residuo/{id}
  - PUT  /api/residuo/{id}
  - DELETE /api/residuo/{id}

- Manutenção
  - POST /api/manutencao
  - GET  /api/manutencao/{id}
  - GET  /api/manutencao/cliente/{clienteId}
  - DELETE /api/manutencao/{id}

---

## Payloads e exemplos

Observação: os exemplos usam JSON. Substitir `<UUID-do-cliente>` e `<UUID-do-residuo>` por valores reais do banco.

1) Criar Licença (POST /api/licenca)

Request JSON:

```json
{
  "idCliente": "<UUID-do-cliente>",
  "tipoLicenca": "ANUAL",
  "dataEmissao": "2025-01-01",
  "dataValidade": "2026-01-01",
  "status": "ATIVA"
}
```

Response (exemplo): 200 OK

```json
{
  "idLicenca": "<UUID-da-licenca>",
  "idCliente": "<UUID-do-cliente>",
  "tipoLicenca": "ANUAL",
  "dataEmissao": "2025-01-01",
  "dataValidade": "2026-01-01",
  "status": "ATIVA"
}
```

2) Listar licenças de um cliente (GET /api/licenca/cliente/{clienteId})

Response: 200 OK — array de objetos `LicencaDTO`.

3) Criar Resíduo (POST /api/residuo)

Request JSON:

```json
{
  "nomeResiduo": "Papel",
  "unidadeMedida": "kg",
  "quantidadeAcumulada": 0.0,
  "limiteReciclagem": 100.0
}
```

Response: 200 OK com `ResiduoDTO` incluindo `idResiduo`.

4) Criar Manutenção com itens de resíduo (POST /api/manutencao)

Request JSON:

```json
{
  "idCliente": "<UUID-do-cliente>",
  "tipoManutencao": 1,
  "dataManutencao": "2025-11-05",
  "residuos": [
    { "idResiduo": "<UUID-do-residuo>", "quantidade": 12.5 },
    { "idResiduo": "<UUID-do-residuo-2>", "quantidade": 3.0 }
  ]
}
```

Response: 200 OK com `ManutencaoDTO` incluindo `idManutencao` e lista de `ManutencaoResiduoDTO`.

---

## Exemplos com PowerShell (Invoke-RestMethod)

Criar licença:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/licenca -ContentType 'application/json' -Body '{
  "idCliente":"<UUID-do-cliente>","tipoLicenca":"ANUAL","dataEmissao":"2025-01-01","dataValidade":"2026-01-01","status":"ATIVA"
}'
```

Criar resíduo:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/residuo -ContentType 'application/json' -Body '{"nomeResiduo":"Papel","unidadeMedida":"kg","quantidadeAcumulada":0.0,"limiteReciclagem":100.0}'
```

Criar manutenção (assumindo resíduos e cliente existentes):

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/manutencao -ContentType 'application/json' -Body '{
  "idCliente":"<UUID-do-cliente>",
  "tipoManutencao":1,
  "dataManutencao":"2025-11-05",
  "residuos":[{"idResiduo":"<UUID-do-residuo>","quantidade":12.5}]
}'
```

---

## Observações importantes

- Migrations: Editei `V1__create_cliente_table.sql` para adicionar `licenca_ambiental`. Se o banco já tiver aplicado V1, crie `V2__create_licenca_ambiental.sql` em vez de modificar V1.
- Autenticação: o endpoint `/api/client/profile` usa token JWT via cabeçalho `Authorization: Bearer <token>`. Outros endpoints não têm restrições explícitas de papel (`@PreAuthorize`) neste momento — adicione regras conforme política de segurança desejada.
- Validação: DTOs têm anotações básicas; erros de payload geram exceções tratadas pelo `GlobalExceptionHandler` do projeto.

---

## Próximos passos sugeridos

1. Criar migrações dedicadas (V2, V3) para as novas tabelas em vez de editar V1.
2. Adicionar testes de integração para os controllers recém-criados.
3. Proteger endpoints com `@PreAuthorize` conforme perfis (ADMIN/USER).

---

Se quiser, eu:
- gero as migrations V2/V3 automaticamente;
- crio exemplos de testes (JUnit + MockMvc) para os controllers;
- testo os endpoints localmente (posso criar um cliente e resíduos de exemplo antes).

Avise qual próximo passo prefere.
