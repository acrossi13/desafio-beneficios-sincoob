# Desafio Fullstack Integrado — Benefícios (DB + EJB + Backend)

Aplicação em camadas para gerenciamento de benefícios e transferência de saldo entre benefícios.

## ✅ O que esta sendo entregue

- DB: scripts de schema e seed
- EJB: correção do bug de transferência (validações + consistência + locking)
- Backend (Spring Boot):
    - CRUD de Benefício
    - Endpoint de transferência
    - Tratamento global de erros (JSON padronizado)
    - Testes (service + controller)
    - ✅ Swagger/OpenAPI (OK)

Frontend Angular: pendente (será feito por último)

---

## Estrutura do projeto

desafio-beneficios-sincoob
├── ejb-module
│   └── src/main/java/com/example/ejb/...
├── backend-module
│   ├── src/main/java/com/example/backend/...
│   └── src/main/resources/db/schema.sql
│   └── src/main/resources/db/seed.sql
└── pom.xml

---

## Como rodar (local)

Requisitos:
- Java 17+
- Maven 3.9+

Build + testes (tudo) — na raiz:
mvn -U clean verify

Rodar backend:
mvn -pl backend-module spring-boot:run

Backend:
http://localhost:8080

Health check:
GET http://localhost:8080/actuator/health

Resposta esperada:
{
"status": "UP"
}

---

## Swagger / OpenAPI

A documentação da API está disponível via Swagger/OpenAPI (já configurado no projeto).
Acesse a UI do Swagger no backend em execução (porta 8080).
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs

---

## Banco / Dados iniciais

Scripts:
- backend-module/src/main/resources/db/schema.sql
- backend-module/src/main/resources/db/seed.sql

Obs: execução local usa H2 em memória (conforme application.properties).

---

## API — Endpoints e retornos

Base URL:
http://localhost:8080/api/beneficios

1) Listar benefícios
   GET /api/beneficios

200:
[
{
"id": 1,
"nome": "Beneficio A",
"descricao": "Descrição A",
"valor": 1000.00,
"ativo": true,
"version": 0
},
{
"id": 2,
"nome": "Beneficio B",
"descricao": "Descrição B",
"valor": 500.00,
"ativo": true,
"version": 0
}
]

2) Buscar benefício por ID
   GET /api/beneficios/{id}

200:
{
"id": 1,
"nome": "Beneficio A",
"descricao": "Descrição A",
"valor": 1000.00,
"ativo": true,
"version": 0
}

404:
{
"timestamp": "2026-02-09T22:26:03.739156Z",
"status": 404,
"error": "Not Found",
"message": "benefício não encontrado: 999",
"path": "/api/beneficios/999"
}

3) Criar benefício
   POST /api/beneficios

Body:
{
"nome": "Benefício teste",
"descricao": "Criado via curl",
"valor": 100.00,
"ativo": true
}

201:
{
"id": 3,
"nome": "Benefício teste",
"descricao": "Criado via curl",
"valor": 100.00,
"ativo": true,
"version": 0
}

4) Atualizar benefício
   PUT /api/beneficios/{id}

Body:
{
"nome": "Benefício atualizado",
"descricao": "Descrição A",
"valor": 120.00,
"ativo": true
}

200:
{
"id": 1,
"nome": "Benefício atualizado",
"descricao": "Descrição A",
"valor": 120.00,
"ativo": true,
"version": 1
}

404:
{
"timestamp": "2026-02-09T22:48:49.607044Z",
"status": 404,
"error": "Not Found",
"message": "benefício não encontrado: 999",
"path": "/api/beneficios/999"
}

5) Remover benefício
   DELETE /api/beneficios/{id}

204 No Content

404:
{
"timestamp": "2026-02-09T22:52:39.021113Z",
"status": 404,
"error": "Not Found",
"message": "benefício não encontrado: 999",
"path": "/api/beneficios/999"
}

6) Transferência de saldo
   POST /api/beneficios/transfer

Body:
{
"fromId": 1,
"toId": 2,
"valor": 10
}

204 No Content

400 (valor inválido):
{
"timestamp": "2026-02-09T22:23:51.352610Z",
"status": 400,
"error": "Bad Request",
"message": "valor inválido",
"path": "/api/beneficios/transfer"
}

409 (saldo insuficiente):
{
"timestamp": "2026-02-09T22:25:18.246686Z",
"status": 409,
"error": "Conflict",
"message": "saldo insuficiente",
"path": "/api/beneficios/transfer"
}

404 (benefício inexistente):
{
"timestamp": "2026-02-09T22:26:03.739156Z",
"status": 404,
"error": "Not Found",
"message": "benefício não encontrado: 999",
"path": "/api/beneficios/transfer"
}

---

## Tratamento de erros (Backend)

Implementado GlobalExceptionHandler retornando ApiError:
- timestamp
- status
- error
- message
- path

Mapeamentos:
- IllegalArgumentException -> 400
- EntityNotFoundException -> 404
- IllegalStateException -> 409
- MethodArgumentTypeMismatchException -> 400 ("parâmetro inválido")
- MethodArgumentNotValidException -> 400 ("payload inválido")
- Exception -> 500 ("erro inesperado")

---

## Correção do bug no EJB

Transferência corrigida para:
- Validar fromId, toId e valor
- Impedir IDs iguais
- Verificar saldo
- Lock pessimista + ordem de locks para evitar deadlock
- Garantir rollback transacional

---

## Testes

Rodar tudo:
mvn -U clean verify

---

## Próximos passos (pendente)

- Frontend Angular consumindo o backend