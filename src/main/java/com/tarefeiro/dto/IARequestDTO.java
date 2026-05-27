package com.tarefeiro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IARequestDTO {
    @NotBlank(message = "O texto é obrigatório")
    private String texto;
    private String tipo; // RESUMIR, INTERPRETAR, CRIAR_REGRA, CRIAR_CHECKLIST
    private String contexto;
}
