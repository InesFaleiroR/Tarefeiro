package com.tarefeiro.api.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WebhookPayload {
    @NotBlank(message = "O tipo de evento é obrigatório")
    private String tipo;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;
    private String fonte;

    // Quaisquer campos extras vão para o mapa de dados
    private Map<String, Object> dados = new HashMap<>();

    @JsonAnySetter
    public void setDado(String chave, Object valor) {
        this.dados.put(chave, valor);
    }
}
