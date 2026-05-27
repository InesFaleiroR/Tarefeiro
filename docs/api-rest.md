# API REST — O Tarefeiro v1.0

Base URL: `http://localhost:8080/api/v1`

## Autenticação

### POST /auth/login
Obtém os tokens JWT.

**Body:**
```json
{ "email": "admin@tarefeiro.pt", "senha": "admin123" }
```
**Response:**
```json
{
  "sucesso": true,
  "dados": {
    "accessToken": "eyJ...",
    "refreshToken": "eyJ...",
    "tipo": "Bearer",
    "expiresIn": 86400,
    "email": "admin@tarefeiro.pt",
    "nome": "Administrador",
    "role": "ADMIN"
  }
}
```

### POST /auth/refresh
Renova o access token.
Header: `Authorization: Bearer <refresh_token>`

### GET /auth/me
Dados do utilizador autenticado.
Header: `Authorization: Bearer <access_token>`

---

## Webhooks

### POST /webhooks/evento (JWT)
Header: `Authorization: Bearer <token>`

### POST /webhooks/externo (Secret)
Headers:
- `X-Webhook-Secret: <secret>`
- `X-Webhook-User-Email: joao@exemplo.pt`
- `X-Webhook-Source: zapier` (opcional)

### POST /webhooks/gmail
Headers: `X-Webhook-Secret`, `X-Webhook-User-Email`
Body: qualquer objeto JSON com campos do Gmail

### POST /webhooks/link
Headers: `X-Webhook-Secret`, `X-Webhook-User-Email`
Body: `{ "url": "https://...", "titulo": "Artigo interessante" }`

### GET /webhooks/health
Público — verifica se o webhook está ativo.

---

## Regras

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /regras | Lista todas as regras |
| GET | /regras/ativas | Só regras ativas |
| GET | /regras/{id} | Detalhe |
| POST | /regras | Criar regra |
| PUT | /regras/{id} | Atualizar regra |
| PATCH | /regras/{id}/alternar | Ativar/desativar |
| DELETE | /regras/{id} | Eliminar |

## Eventos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /eventos | Lista (paginada) |
| GET | /eventos/pendentes | Eventos por processar |
| GET | /eventos/{id} | Detalhe |
| POST | /eventos | Criar evento manual |
| GET | /eventos/stats | Estatísticas |

## Dashboard

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /dashboard | Stats completas |
| GET | /dashboard/notificacoes | Notificações não lidas |
| PATCH | /dashboard/notificacoes/ler-todas | Marcar todas como lidas |
| GET | /dashboard/health | Health check |
