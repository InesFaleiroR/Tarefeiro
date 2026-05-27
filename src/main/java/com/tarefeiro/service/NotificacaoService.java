package com.tarefeiro.service;

import com.tarefeiro.model.Notificacao;

import java.util.List;

public interface NotificacaoService {
    Notificacao criar(Long utilizadorId, String titulo, String mensagem, String tipo);
    List<Notificacao> listarPorUtilizador(Long utilizadorId);
    List<Notificacao> listarNaoLidas(Long utilizadorId);
    void marcarLida(Long id);
    void marcarTodasLidas(Long utilizadorId);
    long contarNaoLidas(Long utilizadorId);
    void limparLidas(Long utilizadorId);
}
