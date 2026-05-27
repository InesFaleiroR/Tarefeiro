package com.tarefeiro.service.impl;

import com.tarefeiro.dto.DashboardDTO;
import com.tarefeiro.dto.EventoDTO;
import com.tarefeiro.dto.ExecucaoAcaoDTO;
import com.tarefeiro.enums.EstadoExecucao;
import com.tarefeiro.exception.ResourceNotFoundException;
import com.tarefeiro.model.ExecucaoAcao;
import com.tarefeiro.repository.*;
import com.tarefeiro.service.EventoService;
import com.tarefeiro.service.ExecucaoAcaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class ExecucaoAcaoServiceImpl implements ExecucaoAcaoService {
    private final ExecucaoAcaoRepository execucaoRepository;
    private final RegraAutomacaoRepository regraRepository;
    private final EventoRepository eventoRepository;
    private final EventoService eventoService;

    @Override
    public ExecucaoAcao buscarPorId(Long id) {
        return execucaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Execução", id));
    }

    @Override
    public Page<ExecucaoAcao> listarPorUtilizador(Long uid, Pageable pageable) {
        return execucaoRepository.findByUtilizadorIdOrderByIniciadoEmDesc(uid, pageable);
    }

    @Override
    public List<ExecucaoAcaoDTO> buscarRecentes(Long uid, int limite) {
        return execucaoRepository.findRecentes(uid, PageRequest.of(0, limite)).stream()
            .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public DashboardDTO obterDashboard(Long uid) {
        return DashboardDTO.builder()
            .totalRegras(regraRepository.count())
            .regrasAtivas(regraRepository.countByUtilizadorIdAndAtivaTrue(uid))
            .totalEventos(eventoRepository.countByUtilizadorId(uid))
            .eventosPendentes(eventoRepository.countByUtilizadorIdAndProcessadoFalse(uid))
            .totalExecucoes(execucaoRepository.countByUtilizadorIdAndEstado(uid, EstadoExecucao.SUCESSO)
                + execucaoRepository.countByUtilizadorIdAndEstado(uid, EstadoExecucao.FALHA))
            .execucoesSucesso(execucaoRepository.countByUtilizadorIdAndEstado(uid, EstadoExecucao.SUCESSO))
            .execucoesFalha(execucaoRepository.countByUtilizadorIdAndEstado(uid, EstadoExecucao.FALHA))
            .execucoesRecentes(buscarRecentes(uid, 5))
            .build();
    }

    private ExecucaoAcaoDTO toDTO(ExecucaoAcao e) {
        return ExecucaoAcaoDTO.builder()
            .id(e.getId()).uuid(e.getUuid())
            .regraNome(e.getRegra().getNome()).acaoTipo(e.getRegra().getAcaoTipo())
            .estado(e.getEstado()).resultado(e.getResultado()).erroDetalhes(e.getErroDetalhes())
            .duracaoMs(e.getDuracaoMs()).tentativas(e.getTentativas())
            .iniciadoEm(e.getIniciadoEm()).concluidoEm(e.getConcluidoEm())
            .regraId(e.getRegra().getId())
            .eventoId(e.getEvento() != null ? e.getEvento().getId() : null)
            .build();
    }
}
