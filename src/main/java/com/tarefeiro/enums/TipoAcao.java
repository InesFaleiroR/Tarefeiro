package com.tarefeiro.enums;

public enum TipoAcao {
    ARQUIVAR_EMAIL("Arquivar Email"),
    RESUMIR_LINK("Resumir Link"),
    CRIAR_CHECKLIST("Criar Checklist"),
    ENVIAR_NOTIFICACAO("Enviar Notificação"),
    ENVIAR_EMAIL("Enviar Email"),
    CRIAR_TAREFA("Criar Tarefa"),
    WEBHOOK_EXTERNO("Webhook Externo"),
    GUARDAR_FICHEIRO("Guardar Ficheiro");

    private final String descricao;

    TipoAcao(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
