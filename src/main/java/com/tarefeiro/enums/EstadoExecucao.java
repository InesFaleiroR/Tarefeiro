package com.tarefeiro.enums;

public enum EstadoExecucao {
    PENDENTE("Pendente"),
    EXECUTANDO("A Executar"),
    SUCESSO("Sucesso"),
    FALHA("Falha"),
    CANCELADO("Cancelado");

    private final String descricao;

    EstadoExecucao(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
