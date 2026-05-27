-- ====================================================
-- O TAREFEIRO - Esquema da Base de Dados v1
-- ====================================================

-- Ativação das extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- ====================================================
-- TABELA: utilizadores
-- ====================================================
CREATE TABLE utilizadores (
                              id              BIGSERIAL PRIMARY KEY,
                              uuid            UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
                              nome            VARCHAR(100) NOT NULL,
                              email           VARCHAR(150) NOT NULL UNIQUE,
                              senha           VARCHAR(255) NOT NULL,
                              role            VARCHAR(20) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
                              ativo           BOOLEAN NOT NULL DEFAULT TRUE,
                              foto_perfil     VARCHAR(255),
                              ultimo_acesso   TIMESTAMP,
                              criado_em       TIMESTAMP NOT NULL DEFAULT NOW(),
                              atualizado_em   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_utilizadores_email ON utilizadores(email);
CREATE INDEX idx_utilizadores_uuid ON utilizadores(uuid);

-- ====================================================
-- TABELA: regras_automacao
-- ====================================================
CREATE TABLE regras_automacao (
                                  id              BIGSERIAL PRIMARY KEY,
                                  uuid            UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
                                  utilizador_id   BIGINT NOT NULL REFERENCES utilizadores(id) ON DELETE CASCADE,
                                  nome            VARCHAR(200) NOT NULL,
                                  descricao       TEXT,
                                  condicao_tipo   VARCHAR(50) NOT NULL,
                                  condicao_valor  TEXT NOT NULL,
                                  acao_tipo       VARCHAR(50) NOT NULL,
                                  acao_config     JSONB NOT NULL DEFAULT '{}',
                                  ativa           BOOLEAN NOT NULL DEFAULT TRUE,
                                  prioridade      INTEGER NOT NULL DEFAULT 0,
                                  execucoes_total INTEGER NOT NULL DEFAULT 0,
                                  ultima_execucao TIMESTAMP,
                                  criado_em       TIMESTAMP NOT NULL DEFAULT NOW(),
                                  atualizado_em   TIMESTAMP NOT NULL DEFAULT NOW(),
                                  CONSTRAINT chk_prioridade CHECK (prioridade >= 0 AND prioridade <= 100)
);

CREATE INDEX idx_regras_utilizador ON regras_automacao(utilizador_id);
CREATE INDEX idx_regras_ativa ON regras_automacao(ativa);
CREATE INDEX idx_regras_prioridade ON regras_automacao(prioridade DESC);
CREATE INDEX idx_regras_condicao ON regras_automacao(condicao_tipo);

-- ====================================================
-- TABELA: eventos
-- ====================================================
CREATE TABLE eventos (
                         id              BIGSERIAL PRIMARY KEY,
                         uuid            UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
                         utilizador_id   BIGINT NOT NULL REFERENCES utilizadores(id) ON DELETE CASCADE,
                         tipo            VARCHAR(50) NOT NULL,
                         titulo          VARCHAR(300) NOT NULL,
                         descricao       TEXT,
                         fonte           VARCHAR(100),
                         payload         JSONB NOT NULL DEFAULT '{}',
                         processado      BOOLEAN NOT NULL DEFAULT FALSE,
                         regra_id        BIGINT REFERENCES regras_automacao(id) ON DELETE SET NULL,
                         criado_em       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_eventos_utilizador ON eventos(utilizador_id);
CREATE INDEX idx_eventos_tipo ON eventos(tipo);
CREATE INDEX idx_eventos_processado ON eventos(processado);
CREATE INDEX idx_eventos_criado ON eventos(criado_em DESC);

-- ====================================================
-- TABELA: execucoes_acao
-- ====================================================
CREATE TABLE execucoes_acao (
                                id              BIGSERIAL PRIMARY KEY,
                                uuid            UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
                                regra_id        BIGINT NOT NULL REFERENCES regras_automacao(id) ON DELETE CASCADE,
                                evento_id       BIGINT REFERENCES eventos(id) ON DELETE SET NULL,
                                utilizador_id   BIGINT NOT NULL REFERENCES utilizadores(id) ON DELETE CASCADE,
                                estado          VARCHAR(20) NOT NULL DEFAULT 'PENDENTE' CHECK (estado IN ('PENDENTE','EXECUTANDO','SUCESSO','FALHA','CANCELADO')),
                                resultado       TEXT,
                                erro_detalhes   TEXT,
                                duracao_ms      INTEGER,
                                tentativas      INTEGER NOT NULL DEFAULT 0,
                                iniciado_em     TIMESTAMP NOT NULL DEFAULT NOW(),
                                concluido_em    TIMESTAMP,
                                metadata        JSONB NOT NULL DEFAULT '{}'
);

CREATE INDEX idx_execucoes_regra ON execucoes_acao(regra_id);
CREATE INDEX idx_execucoes_utilizador ON execucoes_acao(utilizador_id);
CREATE INDEX idx_execucoes_estado ON execucoes_acao(estado);
CREATE INDEX idx_execucoes_iniciado ON execucoes_acao(iniciado_em DESC);

-- ====================================================
-- TABELA: resumos_ia (cache de respostas IA)
-- ====================================================
CREATE TABLE resumos_ia (
                            id              BIGSERIAL PRIMARY KEY,
                            utilizador_id   BIGINT NOT NULL REFERENCES utilizadores(id) ON DELETE CASCADE,
                            tipo            VARCHAR(50) NOT NULL,
                            entrada         TEXT NOT NULL,
                            saida           TEXT NOT NULL,
                            tokens_usados   INTEGER,
                            modelo          VARCHAR(50),
                            criado_em       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_resumos_utilizador ON resumos_ia(utilizador_id);
CREATE INDEX idx_resumos_tipo ON resumos_ia(tipo);

-- ====================================================
-- TABELA: notificacoes
-- ====================================================
CREATE TABLE notificacoes (
                              id              BIGSERIAL PRIMARY KEY,
                              utilizador_id   BIGINT NOT NULL REFERENCES utilizadores(id) ON DELETE CASCADE,
                              titulo          VARCHAR(200) NOT NULL,
                              mensagem        TEXT NOT NULL,
                              tipo            VARCHAR(30) NOT NULL DEFAULT 'INFO' CHECK (tipo IN ('INFO','SUCESSO','AVISO','ERRO')),
                              lida            BOOLEAN NOT NULL DEFAULT FALSE,
                              link            VARCHAR(500),
                              criado_em       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notificacoes_utilizador ON notificacoes(utilizador_id);
CREATE INDEX idx_notificacoes_lida ON notificacoes(lida);
CREATE INDEX idx_notificacoes_criado ON notificacoes(criado_em DESC);

-- ====================================================
-- FUNÇÃO E TRIGGERS: atualizar timestamp automaticamente
-- ====================================================
CREATE OR REPLACE FUNCTION atualizar_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_utilizadores_atualizado
    BEFORE UPDATE ON utilizadores
    FOR EACH ROW EXECUTE FUNCTION atualizar_timestamp();

CREATE TRIGGER trg_regras_atualizado
    BEFORE UPDATE ON regras_automacao
    FOR EACH ROW EXECUTE FUNCTION atualizar_timestamp();

-- ====================================================
-- VIEW: painel de controlo do utilizador
-- ====================================================
CREATE OR REPLACE VIEW v_dashboard_utilizador AS
SELECT
    u.id AS utilizador_id,
    u.nome,
    COUNT(DISTINCT r.id) AS total_regras,
    COUNT(DISTINCT r.id) FILTER (WHERE r.ativa) AS regras_ativas,
    COUNT(DISTINCT e.id) AS total_eventos,
    COUNT(DISTINCT e.id) FILTER (WHERE NOT e.processado) AS eventos_pendentes,
    COUNT(DISTINCT ex.id) AS total_execucoes,
    COUNT(DISTINCT ex.id) FILTER (WHERE ex.estado = 'SUCESSO') AS execucoes_sucesso,
    COUNT(DISTINCT ex.id) FILTER (WHERE ex.estado = 'FALHA') AS execucoes_falha
FROM utilizadores u
         LEFT JOIN regras_automacao r ON r.utilizador_id = u.id
         LEFT JOIN eventos e ON e.utilizador_id = u.id
         LEFT JOIN execucoes_acao ex ON ex.utilizador_id = u.id
GROUP BY u.id, u.nome;
