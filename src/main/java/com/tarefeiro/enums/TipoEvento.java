package com.tarefeiro.enums;

public enum TipoEvento {
    EMAIL_RECEBIDO("Email Recebido"),
    LINK_PARTILHADO("Link Partilhado"),
    REUNIAO_AGENDADA("Reunião Agendada"),
    TAREFA_CRIADA("Tarefa Criada"),
    FICHEIRO_CARREGADO("Ficheiro Carregado"),
    MENSAGEM_RECEBIDA("Mensagem Recebida"),
    WEBHOOK("Webhook"),
    MANUAL("Manual");

    private final String descricao;

    TipoEvento(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
