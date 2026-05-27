package com.tarefeiro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean sucesso;
    private String mensagem;
    private T dados;
    private Integer total;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> ok(T dados) {
        return ApiResponse.<T>builder().sucesso(true).dados(dados).build();
    }

    public static <T> ApiResponse<T> ok(T dados, String mensagem) {
        return ApiResponse.<T>builder().sucesso(true).dados(dados).mensagem(mensagem).build();
    }

    public static <T> ApiResponse<T> erro(String mensagem) {
        return ApiResponse.<T>builder().sucesso(false).mensagem(mensagem).build();
    }

    public static <T> ApiResponse<T> lista(T dados, int total) {
        return ApiResponse.<T>builder().sucesso(true).dados(dados).total(total).build();
    }
}
