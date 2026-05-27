# Endpoints — O Tarefeiro

## Autenticação
- `GET /login` — Página de login
- `POST /login` — Processar login
- `GET /logout` — Terminar sessão
- `GET /utilizadores/registo` — Página de registo
- `POST /utilizadores/registo` — Criar conta

## Dashboard
- `GET /dashboard` — Dashboard principal

## Regras
- `GET /regras` — Listar regras
- `GET /regras/nova` — Formulário nova regra
- `POST /regras/nova` — Criar regra
- `GET /regras/{id}` — Detalhe da regra
- `GET /regras/{id}/editar` — Editar regra
- `POST /regras/{id}/editar` — Atualizar regra
- `POST /regras/{id}/alternar` — Ativar/desativar regra
- `POST /regras/{id}/excluir` — Eliminar regra

## Eventos
- `GET /eventos` — Listar eventos
- `GET /eventos/novo` — Formulário novo evento
- `POST /eventos/novo` — Criar evento
- `GET /eventos/{id}` — Detalhe do evento

## Execuções
- `GET /execucoes` — Histórico de execuções
- `GET /execucoes/{id}` — Detalhe da execução

## IA
- `GET /ia` — Página do assistente IA
- `POST /ia/processar` — Processar pedido de IA
