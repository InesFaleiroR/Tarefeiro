package com.tarefeiro.enums;

public enum TipoCondicao {
    EMAIL_ASSUNTO_CONTEM("Email: assunto contém"),
    EMAIL_REMETENTE_CONTEM("Email: remetente contém"),
    EMAIL_PRIORIDADE("Email: prioridade"),
    LINK_DOMINIO_CONTEM("Link: domínio contém"),
    LINK_TITULO_CONTEM("Link: título contém"),
    EVENTO_TIPO("Tipo de evento"),
    SEMPRE("Sempre (sem condição)");

    private final String descricao;

    TipoCondicao(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
