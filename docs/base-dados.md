# Base de Dados — O Tarefeiro

## Tabelas

### `utilizadores`
Utilizadores da aplicação. Suporte a roles USER/ADMIN.

### `regras_automacao`
Regras no formato SE → ENTÃO. Cada regra tem condição (tipo + valor) e ação (tipo + config JSON).

### `eventos`
Eventos que despoletam as regras. Podem ser criados manualmente ou via integração.

### `execucoes_acao`
Histórico de todas as execuções de regras, com estado, resultado e duração.

### `resumos_ia`
Cache de respostas da IA para evitar chamadas redundantes à API.

### `notificacoes`
Sistema de notificações in-app por utilizador.

## Views
- `v_dashboard_utilizador` — agregações por utilizador para o dashboard.
