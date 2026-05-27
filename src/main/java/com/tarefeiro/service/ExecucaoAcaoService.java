package com.tarefeiro.service;

import com.tarefeiro.dto.DashboardDTO;
import com.tarefeiro.dto.ExecucaoAcaoDTO;
import com.tarefeiro.model.ExecucaoAcao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExecucaoAcaoService {
    ExecucaoAcao buscarPorId(Long id);
    Page<ExecucaoAcao> listarPorUtilizador(Long utilizadorId, Pageable pageable);
    List<ExecucaoAcaoDTO> buscarRecentes(Long utilizadorId, int limite);
    DashboardDTO obterDashboard(Long utilizadorId);
}
