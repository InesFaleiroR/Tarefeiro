package com.tarefeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RegraDTO {
    private Long id;
    private UUID uuid;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 200, message = "O nome deve ter entre 3 e 200 caracteres")
    private String nome;

    private String descricao;

    @NotBlank(message = "O tipo de condição é obrigatório")
    private String condicaoTipo;

    @NotBlank(message = "O valor da condição é obrigatório")
    private String condicaoValor;

    @NotBlank(message = "O tipo de ação é obrigatório")
    private String acaoTipo;

    private String acaoConfig;
    private Boolean ativa;
    private Integer prioridade;
    private Integer execucoesTotal;
    private LocalDateTime ultimaExecucao;
    private LocalDateTime criadoEm;
    private Long utilizadorId;
}
