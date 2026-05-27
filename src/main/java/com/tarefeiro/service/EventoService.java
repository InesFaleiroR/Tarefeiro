package com.tarefeiro.service;

import com.tarefeiro.dto.EventoDTO;
import com.tarefeiro.model.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventoService {
    Evento criar(EventoDTO dto, Long utilizadorId);
    Evento buscarPorId(Long id);
    Page<Evento> listarPorUtilizador(Long utilizadorId, Pageable pageable);
    List<Evento> listarNaoProcessados(Long utilizadorId);
    void marcarProcessado(Long id);
    long contarPendentes(Long utilizadorId);
}
