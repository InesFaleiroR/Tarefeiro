package com.tarefeiro.dto;

import com.tarefeiro.enums.EstadoExecucao;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExecucaoAcaoDTO {
    private Long id;
    private UUID uuid;
    private String regraNome;
    private String acaoTipo;
    private EstadoExecucao estado;
    private String resultado;
    private String erroDetalhes;
    private Integer duracaoMs;
    private Integer tentativas;
    private LocalDateTime iniciadoEm;
    private LocalDateTime concluidoEm;
    private Long regraId;
    private Long eventoId;
}
