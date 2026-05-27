package com.tarefeiro.service;

import com.tarefeiro.dto.RegraDTO;
import com.tarefeiro.model.RegraAutomacao;

import java.util.List;

public interface RegraAutomacaoService {
    RegraAutomacao criar(RegraDTO dto, Long utilizadorId);
    RegraAutomacao atualizar(Long id, RegraDTO dto, Long utilizadorId);
    void excluir(Long id, Long utilizadorId);
    RegraAutomacao buscarPorId(Long id);
    List<RegraAutomacao> listarPorUtilizador(Long utilizadorId);
    List<RegraAutomacao> listarAtivasPorUtilizador(Long utilizadorId);
    List<RegraAutomacao> pesquisar(Long utilizadorId, String query);
    void alternarAtiva(Long id, Long utilizadorId);
}
