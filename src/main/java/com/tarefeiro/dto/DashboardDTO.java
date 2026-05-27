package com.tarefeiro.dto;

import lombok.*;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardDTO {
    private long totalRegras;
    private long regrasAtivas;
    private long totalEventos;
    private long eventosPendentes;
    private long totalExecucoes;
    private long execucoesSucesso;
    private long execucoesFalha;
    private long notificacoesNaoLidas;
    private List<ExecucaoAcaoDTO> execucoesRecentes;
    private List<EventoDTO> eventosRecentes;
}
