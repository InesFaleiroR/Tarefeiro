package com.tarefeiro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tipo;
    private Long expiresIn;
    private String email;
    private String nome;
    private String role;
}
