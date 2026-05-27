-- V3: Tabela para Remember-Me persistente (Spring Security)
-- Criada separada para não interferir com a migração principal

CREATE TABLE IF NOT EXISTS persistent_logins (
    username  VARCHAR(64)  NOT NULL,
    series    VARCHAR(64)  PRIMARY KEY,
    token     VARCHAR(64)  NOT NULL,
    last_used TIMESTAMP    NOT NULL
);
