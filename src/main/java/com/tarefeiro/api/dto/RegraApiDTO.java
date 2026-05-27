package com.tarefeiro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
public class RegraApiDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String condicaoTipo;
    private String condicaoValor;
    private String acaoTipo;
    private String acaoConfig;
    private Boolean ativa;
    private Integer prioridade;
    private Integer execucoesTotal;
    private LocalDateTime ultimaExecucao;
    private LocalDateTime criadoEm;
}
