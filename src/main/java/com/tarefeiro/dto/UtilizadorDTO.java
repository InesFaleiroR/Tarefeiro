package com.tarefeiro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UtilizadorDTO {
    private Long id;
    private UUID uuid;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;

    private String role;
    private Boolean ativo;
    private String fotoPerfil;
    private LocalDateTime ultimoAcesso;
    private LocalDateTime criadoEm;
    private long totalRegras;
    private long totalEventos;
}
