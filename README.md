<div align="center">

# ⚡ O Tarefeiro

### Assistente inteligente de automação com API REST + Webhooks + IA

*"SE isto acontecer → ENTÃO faz aquilo"*

[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue)](https://www.postgresql.org)
[![JWT](https://img.shields.io/badge/JWT-0.12.5-purple)](https://github.com/jwtk/jjwt)
[![OpenAI](https://img.shields.io/badge/OpenAI-GPT--4o--mini-black)](https://platform.openai.com)

</div>

---

## ≽^•⩊•^≼ O que é o Tarefeiro?

O **Tarefeiro** é uma webapp de automação inteligente que combina:
- 🌐 **Interface Web** — Thymeleaf dark-theme completo
- 🔌 **API REST** — autenticada com JWT para integrações programáticas
- 🪝 **Webhooks** — recebe eventos de qualquer sistema externo (Gmail, Zapier, IFTTT…)
- 🤖 **IA integrada** — resumos, checklists e sugestões com OpenAI

> ◕⩊◕ *O teu assistente silencioso que trabalha enquanto dormes.*

---

## （＾ω＾） Funcionalidades Completas

| Funcionalidade                         | Web | API REST         |
|----------------------------------------|-----|------------------|
| ⚡ Motor de Automação SE → ENTÃO        | ✅ | ✅ via webhook    |
| 🤖 Assistente IA (resumos, checklists) | ✅ | -                |
| 🔑 Autenticação JWT                    | -   | ✅                |
| 🪝 Receber webhooks externos           | -   | ✅                |
| 📊 Dashboard de estatísticas           | ✅   | ✅                |
| 🔔 Notificações in-app                 | ✅   | ✅                |
| 🔒 Remember-Me persistente             | ✅   | -                |
| 🌐 CORS configurado                    | -   | ✅                |

---

## ┐('～` )┌ Stack Tecnológico

```
Backend:     Java 21 + Spring Boot 3.2.5
Frontend:    Thymeleaf + CSS Dark Theme + Bootstrap Icons
Base Dados:  PostgreSQL 15+ + Flyway (migrações automáticas)
Segurança:   Spring Security (2 cadeias: Web + API) + BCrypt(12) + JWT
JWT:         JJWT 0.12.5 (io.jsonwebtoken)
ORM:         Spring Data JPA + Hibernate
HTTP Client: OkHttp 4.12.0 (chamadas OpenAI)
Build:       Maven 3.9+
IA:          OpenAI API (GPT-4o-mini, com fallback simulado)
```

---

## ╰(◣﹏◢)╯ Pré-requisitos

- ☕ **Java 21 JDK** → [adoptium.net](https://adoptium.net)
- 🐘 **PostgreSQL 15+** → [postgresql.org](https://www.postgresql.org/download/)
- 🔧 **Maven 3.9+** → [maven.apache.org](https://maven.apache.org)
- 💻 **IntelliJ IDEA** (recomendado)
- 🗄️ **pgAdmin 4** (opcional)

---

## (˶˃⤙˂˶) Instalação Passo a Passo

### 1️⃣ Preparar a Base de Dados PostgreSQL

Abre o **pgAdmin** ou o terminal `psql` e executa:

```sql
-- Criar utilizador (opcional — podes usar o postgres diretamente)
CREATE USER tarefeiro_user WITH PASSWORD 'tarefeiro_pass';

-- Criar a base de dados
CREATE DATABASE tarefeiro_db OWNER tarefeiro_user;

-- Dar todas as permissões
GRANT ALL PRIVILEGES ON DATABASE tarefeiro_db TO tarefeiro_user;
```

> ⸜( ˙˘˙)⸝ **O Flyway cria automaticamente TODAS as tabelas** na primeira execução. Não precisas de correr nenhum SQL manualmente!

---

### 2️⃣ Configurar Variáveis de Ambiente

**Método A — application.yaml** (edita o ficheiro diretamente):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tarefeiro_db
    username: tarefeiro_user
    password: tarefeiro_pass
```

**Método B — Variáveis de ambiente** (recomendado para produção):
```bash
# Linux/macOS — adiciona ao ~/.bashrc ou ~/.zshrc
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=tarefeiro_db
export DB_USER=tarefeiro_user
export DB_PASSWORD=tarefeiro_pass

# JWT (OBRIGATÓRIO alterar em produção!)
export JWT_SECRET=$(openssl rand -base64 64)
export WEBHOOK_SECRET=meu-secret-seguro-aqui

# OpenAI (opcional — funciona sem ele com respostas simuladas)
export OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxx

# No Windows (Command Prompt)
set DB_HOST=localhost
set DB_PASSWORD=tarefeiro_pass
```

---

### 3️⃣ Correr a Aplicação

```bash
# Navega para a pasta do projeto
cd tarefeiro

# Compilar (a primeira vez demora enquanto descarrega as dependências)
./mvnw clean install -DskipTests

# Executar
./mvnw spring-boot:run

# No Windows
mvnw.cmd spring-boot:run
```

**No IntelliJ IDEA:**
1. `File → Open` → seleciona a pasta `tarefeiro`
2. Aguarda o Maven importar as dependências (barra de progresso em baixo)
3. Clica em ▶️ `Run TarefeiroApplication`

---

### 4️⃣ Aceder à Aplicação

```
🌐 Interface Web → http://localhost:8080
🔌 API REST      → http://localhost:8080/api/v1
🪝 Webhooks      → http://localhost:8080/api/v1/webhooks
📊 Health Check  → http://localhost:8080/actuator/health
```

#### Credenciais de Teste

| Utilizador    | Email                | Senha      | Role  |
|---------------|----------------------|------------|-------|
| Administrador | `admin@tarefeiro.pt` | `admin123` | ADMIN |
| João Silva    | `joao@exemplo.pt`    | `user123`  | USER  |

---

## ≖‿≖ Estrutura do Projeto

```
tarefeiro/
├── pom.xml                              ← Dependências Maven (com JWT)
├── mvnw / mvnw.cmd                      ← Maven Wrapper
├── README.md                            ← Este ficheiro
├── .gitignore
├── docs/
│   ├── api-rest.md                      ← Documentação completa da API
│   ├── automacao.md                     ← Como funciona o motor
│   ├── base-dados.md                    ← Schema da BD
│   └── endpoints.md                     ← Endpoints web
│
└── src/main/java/com/tarefeiro/
    ├── TarefeiroApplication.java
    │
    ├── config/
    │   └── SecurityConfig.java          ← 2 cadeias: Web (sessão) + API (JWT)
    │
    ├── security/                        ← ★ NOVO
    │   ├── JwtUtil.java                 ← Gerar/validar tokens JWT
    │   └── JwtAuthFilter.java           ← Filtro que intercepta /api/**
    │
    ├── api/                             ← ★ NOVO — API REST completa
    │   ├── controller/
    │   │   ├── AuthApiController.java   ← /api/v1/auth/{login,refresh,me}
    │   │   ├── RegraApiController.java  ← /api/v1/regras CRUD
    │   │   ├── EventoApiController.java ← /api/v1/eventos
    │   │   └── DashboardApiController.java
    │   ├── webhook/
    │   │   └── WebhookController.java   ← /api/v1/webhooks (JWT + Secret)
    │   └── dto/
    │       ├── AuthRequest/Response.java
    │       ├── ApiResponse.java         ← Envelope padrão JSON
    │       ├── WebhookPayload.java
    │       ├── RegraApiDTO.java
    │       └── EventoApiDTO.java
    │
    ├── controller/                      ← Controladores Web (Thymeleaf)
    ├── model/                           ← Entidades JPA
    ├── repository/                      ← Repositórios Spring Data
    ├── service/                         ← Serviços + implementações
    ├── automation/                      ← Motor @Scheduled
    ├── ai/                              ← Integração OpenAI
    ├── dto/                             ← DTOs web
    ├── enums/                           ← Tipos, estados
    └── exception/                       ← Handlers globais (Web + API)
```

---

## 🔌 API REST — Referência Completa

### Autenticação

**1. Fazer login e obter token:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@tarefeiro.pt","senha":"admin123"}'
```

**Resposta:**
```json
{
  "sucesso": true,
  "mensagem": "Login efetuado com sucesso!",
  "dados": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tipo": "Bearer",
    "expiresIn": 86400,
    "email": "admin@tarefeiro.pt",
    "nome": "Administrador",
    "role": "ADMIN"
  }
}
```

**2. Usar o token nas chamadas:**
```bash
# Define o token numa variável
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

# Listar as tuas regras
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/regras
```

**3. Renovar o token (sem fazer login novamente):**
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Authorization: Bearer $REFRESH_TOKEN"
```

---

### Regras de Automação

```bash
# Listar todas as regras
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/regras

# Criar nova regra
curl -X POST http://localhost:8080/api/v1/regras \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Arquivar newsletters via API",
    "condicaoTipo": "EMAIL_ASSUNTO_CONTEM",
    "condicaoValor": "newsletter,promoção",
    "acaoTipo": "ARQUIVAR_EMAIL",
    "acaoConfig": "{\"pasta\":\"Newsletters\"}",
    "ativa": true,
    "prioridade": 10
  }'

# Ativar/desativar regra (toggle)
curl -X PATCH -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/regras/1/alternar

# Eliminar regra
curl -X DELETE -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/regras/1
```

---

### 🪝 Webhooks — Receber Eventos Externos

**Modo 1 — JWT (utilizador autenticado):**
```bash
curl -X POST http://localhost:8080/api/v1/webhooks/evento \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "EMAIL_RECEBIDO",
    "titulo": "Newsletter Tech Weekly",
    "descricao": "As melhores notícias de tecnologia da semana",
    "fonte": "Gmail"
  }'
```

**Modo 2 — Webhook Secret (para sistemas externos sem JWT):**
```bash
curl -X POST http://localhost:8080/api/v1/webhooks/externo \
  -H "X-Webhook-Secret: webhook-secret-alterar-em-producao" \
  -H "X-Webhook-User-Email: joao@exemplo.pt" \
  -H "X-Webhook-Source: Zapier" \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "LINK_PARTILHADO",
    "titulo": "Artigo: The Future of AI",
    "url": "https://medium.com/article-xyz"
  }'
```

**Modo 3 — Endpoint Gmail especializado:**
```bash
curl -X POST http://localhost:8080/api/v1/webhooks/gmail \
  -H "X-Webhook-Secret: webhook-secret-alterar-em-producao" \
  -H "X-Webhook-User-Email: joao@exemplo.pt" \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "Flash Sale — 50% off",
    "from": "loja@exemplo.com",
    "snippet": "Aproveita já esta promoção incrível..."
  }'
```

**Modo 4 — Link (extensão de browser):**
```bash
curl -X POST http://localhost:8080/api/v1/webhooks/link \
  -H "X-Webhook-Secret: webhook-secret-alterar-em-producao" \
  -H "X-Webhook-User-Email: joao@exemplo.pt" \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://news.ycombinator.com/item?id=12345",
    "titulo": "Show HN: My new project"
  }'
```

**Verificar se os webhooks estão ativos (público):**
```bash
curl http://localhost:8080/api/v1/webhooks/health
```

---

### Dashboard API

```bash
# Estatísticas completas
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/dashboard

# Notificações não lidas
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/dashboard/notificacoes

# Marcar todas como lidas
curl -X PATCH -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/dashboard/notificacoes/ler-todas
```

---

### Formato de Resposta Padrão

Todas as respostas da API seguem o envelope:

```json
{
  "sucesso": true,
  "mensagem": "Operação bem-sucedida",
  "dados": { ... },
  "total": 42,
  "timestamp": "2025-05-26T12:00:00"
}
```

**Erros:**
```json
{
  "sucesso": false,
  "mensagem": "Credenciais inválidas.",
  "timestamp": "2025-05-26T12:00:00"
}
```

**Códigos HTTP:**
| Código | Significado |
|---|---|
| 200 | OK |
| 201 | Criado |
| 400 | Validação falhou |
| 401 | Não autenticado (token em falta/inválido) |
| 403 | Sem permissão |
| 404 | Não encontrado |
| 500 | Erro interno |

---

## Fooooood…ԅ(¯﹃¯ԅ) Tipos de Condição e Ação

### Condições (SE):
| Valor | Descrição |
|---|---|
| `EMAIL_ASSUNTO_CONTEM` | Assunto do email contém termo(s) |
| `EMAIL_REMETENTE_CONTEM` | Remetente contém termo |
| `EMAIL_PRIORIDADE` | Email com prioridade alta |
| `LINK_DOMINIO_CONTEM` | Domínio do link contém termo |
| `LINK_TITULO_CONTEM` | Título do link contém termo |
| `EVENTO_TIPO` | Tipo exato do evento |
| `SEMPRE` | Executa sempre |

### Ações (ENTÃO):
| Valor | Descrição | Exemplo config |
|---|---|---|
| `ARQUIVAR_EMAIL` | Arquiva email | `{"pasta":"Newsletters"}` |
| `RESUMIR_LINK` | Resumo IA do link | `{}` |
| `CRIAR_CHECKLIST` | Checklist IA | `{}` |
| `ENVIAR_NOTIFICACAO` | Notificação in-app | `{"mensagem":"Feito!"}` |
| `ENVIAR_EMAIL` | Envia email | `{"para":"eu@mail.pt"}` |
| `CRIAR_TAREFA` | Regista tarefa | `{"lista":"Inbox"}` |

### Tipos de Evento:
| Valor | Descrição |
|---|---|
| `EMAIL_RECEBIDO` | Novo email |
| `LINK_PARTILHADO` | Link partilhado |
| `FICHEIRO_RECEBIDO` | Ficheiro novo |
| `REUNIAO_AGENDADA` | Evento de calendário |
| `TAREFA_CRIADA` | Nova tarefa |
| `ALERTA_SISTEMA` | Alerta do sistema |
| `WEBHOOK_EXTERNO` | Evento via webhook |
| `MANUAL` | Criado manualmente |

---

## ╮(﹀_﹀")╭ Variáveis de Ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_HOST` | `localhost` | Host PostgreSQL |
| `DB_PORT` | `5432` | Porta PostgreSQL |
| `DB_NAME` | `tarefeiro_db` | Nome da BD |
| `DB_USER` | `postgres` | Utilizador BD |
| `DB_PASSWORD` | `postgres` | Senha BD |
| `OPENAI_API_KEY` | `""` | Chave OpenAI (opcional) |
| `OPENAI_MODEL` | `gpt-4o-mini` | Modelo a usar |
| `JWT_SECRET` | *(base64 padrão)* | **ALTERAR EM PRODUÇÃO!** |
| `JWT_EXPIRATION_MS` | `86400000` | Validade do access token (24h) |
| `JWT_REFRESH_MS` | `604800000` | Validade do refresh token (7d) |
| `WEBHOOK_SECRET` | *(valor padrão)* | **ALTERAR EM PRODUÇÃO!** |
| `PORT` | `8080` | Porta HTTP |
| `MAIL_HOST` | `smtp.gmail.com` | Servidor SMTP |
| `MAIL_USER` | `""` | Email para envio |
| `MAIL_PASSWORD` | `""` | Senha do email |

---

## p(╬ Ò ‸ Ó)q Troubleshooting

**❌ `Connection refused` na base de dados**
```bash
# Verifica se o PostgreSQL está ativo
sudo systemctl status postgresql          # Linux
brew services list | grep postgresql     # macOS
# Windows: Serviços → PostgreSQL → Iniciar
```

**❌ `FlywayException: validate failed`**
```sql
-- Apaga o schema e recomeça (CUIDADO: perde todos os dados!)
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
-- Reinicia a app — o Flyway recria tudo automaticamente
```

**❌ `401 Unauthorized` na API**
```bash
# Certifica que estás a enviar o token corretamente
curl -H "Authorization: Bearer eyJ..."
# Nota: é "Bearer " (com espaço) antes do token
```

**❌ `401` no webhook externo**
```bash
# Verifica que o secret bate certo com WEBHOOK_SECRET no application.yaml
# Valor padrão: webhook-secret-alterar-em-producao
curl -H "X-Webhook-Secret: webhook-secret-alterar-em-producao" ...
```

**❌ `Port 8080 already in use`**
```bash
lsof -i :8080 | awk 'NR>1{print $2}' | xargs kill -9  # macOS/Linux
# Ou muda a porta: server.port=8090 em application.yaml
```

**❌ A IA retorna respostas simuladas**
```
A variável OPENAI_API_KEY não está definida. Define-a para respostas reais.
A app funciona normalmente com respostas simuladas.
```

---

## ┌∩┐(◣_◢)┌∩┐ Segurança

### Arquitetura de Segurança (Dual Chain)
```
Pedido HTTP
    │
    ├─ /api/** ──→ [JwtAuthFilter] ──→ Stateless (sem sessão, sem CSRF)
    │                                   ↑ JWT obrigatório
    │
    └─ /** ─────→ [Session + CSRF] ──→ Formulário login + Remember-Me
                                        ↑ Cookie de sessão
```

### Boas Práticas Implementadas
- 🔒 **BCrypt** strength 12 para senhas
- 🛡️ **CSRF** em todos os formulários web
- 🔑 **JWT** RS256-compatível (HS256 por padrão)
- 🕒 **Remember-Me** com tokens BD (não cookie simples)
- ✅ **Validação** Bean Validation em todos os inputs
- 🚫 **Error handling** que não vaza stack traces
- 🌐 **CORS** configurado para a API

> (_　_|||) *Em produção: usa HTTPS, muda o JWT_SECRET e o WEBHOOK_SECRET!*

---

## 凸(⊙▂⊙ ) Integração com Sistemas Externos

### Zapier
1. Cria um Zap com trigger (ex: Gmail)
2. Ação: **Webhook by Zapier → POST**
3. URL: `https://teu-dominio.com/api/v1/webhooks/externo`
4. Headers:
   - `X-Webhook-Secret`: o teu secret
   - `X-Webhook-User-Email`: o teu email
5. Body: `{"tipo":"EMAIL_RECEBIDO","titulo":"{{subject}}"}`

### IFTTT
1. Cria um Applet com trigger Gmail/RSS/etc.
2. Ação: **Webhooks → Make a web request**
3. URL: `https://teu-dominio.com/api/v1/webhooks/externo`
4. Method: POST
5. Content Type: application/json
6. Body: `{"tipo":"EMAIL_RECEBIDO","titulo":"{{Subject}}"}`
7. Headers adicionais: X-Webhook-Secret + X-Webhook-User-Email

### Extensão Browser (Chrome/Firefox)
Usa o endpoint `/api/v1/webhooks/link` para partilhar links automaticamente:
```javascript
fetch('https://teu-dominio.com/api/v1/webhooks/link', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'X-Webhook-Secret': 'o-teu-secret',
    'X-Webhook-User-Email': 'o-teu@email.pt'
  },
  body: JSON.stringify({ url: window.location.href, titulo: document.title })
});
```

---

## (╥﹏╥) Comandos Maven Úteis

```bash
./mvnw clean install -DskipTests   # Compilar (sem testes)
./mvnw spring-boot:run             # Executar em modo dev
./mvnw test                        # Correr testes
./mvnw clean package -DskipTests   # Criar JAR para produção
java -jar target/tarefeiro-1.0.0.jar  # Executar o JAR final

# Verificar dependências
./mvnw dependency:tree
```

---

## (╥﹏╥) Funcionalidades Futuras

- [ ] 🌐 Integração nativa Gmail API (OAuth2)
- [ ] 📱 App móvel (Flutter/React Native)
- [ ] 📊 Gráficos de analytics com Chart.js
- [ ] ⏰ Regras agendadas (cron expressions)
- [ ] 🤝 Integração Slack/Discord/Teams
- [ ] 🔄 Importar/exportar regras (JSON)
- [ ] 🌍 Multi-idioma (i18n)
- [ ] 📤 SDK cliente (Python, Node.js)
- [ ] 🐳 Docker + docker-compose

---

##   /) /) ~ ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
## ( •-• ) ~  Por que é que ainda fazes tarefas
## /づ づ ~    manualmente?! O Tarefeiro faz isso!
##         ~ ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

---

## ＼(o￣∇￣o)/ Licença

MIT License — usa, modifica e distribui!

---

<div align="center">

Feito com ⚡ e ☕

*O Tarefeiro — porque o teu tempo vale mais que tarefas repetitivas.*

> (ദ്ദി˙ᗜ˙) *Boa sorte e boas automações!*

> ≽^•⩊•^≼ *Encontraste um bug? Abre uma issue. Tens uma ideia? Abre um PR.*

</div>
