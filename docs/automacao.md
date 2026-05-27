# Motor de Automação — O Tarefeiro

## Arquitetura

O motor corre como um `@Scheduled` Spring Bean (`MotorAutomacao`) com intervalo configurável (padrão: 60 segundos).

## Fluxo de Execução
1. Motor verifica eventos pendentes de todos os utilizadores ativos
2. Para cada evento, verifica quais regras ativas correspondem (por prioridade)
3. Quando há correspondência, chama `RegraExecutor.executar(regra, evento)`
4. Regista a execução em `execucoes_acao` (sucesso ou falha)
5. Notifica o utilizador em caso de falha

## Tipos de Condição
- `EMAIL_ASSUNTO_CONTEM` — verifica se payload contém termos (vírgula-separados)
- `LINK_DOMINIO_CONTEM` — verifica domínio no payload
- `EVENTO_TIPO` — corresponde ao tipo exato do evento
- `SEMPRE` — sempre executar

## Tipos de Ação
- `ARQUIVAR_EMAIL` — simula arquivamento em pasta
- `RESUMIR_LINK` — gera resumo via IA
- `CRIAR_CHECKLIST` — gera checklist via IA
- `ENVIAR_NOTIFICACAO` — cria notificação in-app
- `CRIAR_TAREFA` — regista tarefa (extensível)
