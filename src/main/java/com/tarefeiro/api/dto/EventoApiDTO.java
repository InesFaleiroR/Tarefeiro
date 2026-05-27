package com.tarefeiro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
public class EventoApiDTO {
    private Long id;
    private String tipo;
    private String titulo;
    private String descricao;
    private String fonte;
    private String payload;
    private Boolean processado;
    private LocalDateTime criadoEm;
}
