package com.tarefeiro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventoDTO {
    private Long id;
    private UUID uuid;

    @NotBlank(message = "O tipo é obrigatório")
    private String tipo;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;
    private String fonte;
    private String payload;
    private Boolean processado;
    private LocalDateTime criadoEm;
    private Long utilizadorId;
    private Long regraId;
}
