package com.tarefeiro.service.impl;

import com.tarefeiro.dto.RegraDTO;
import com.tarefeiro.exception.ResourceNotFoundException;
import com.tarefeiro.model.RegraAutomacao;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.repository.RegraAutomacaoRepository;
import com.tarefeiro.repository.UtilizadorRepository;
import com.tarefeiro.service.RegraAutomacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor @Slf4j @Transactional
public class RegraAutomacaoServiceImpl implements RegraAutomacaoService {
    private final RegraAutomacaoRepository repository;
    private final UtilizadorRepository utilizadorRepository;

    @Override
    public RegraAutomacao criar(RegraDTO dto, Long utilizadorId) {
        Utilizador u = utilizadorRepository.findById(utilizadorId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilizador", utilizadorId));
        RegraAutomacao r = RegraAutomacao.builder()
            .utilizador(u).nome(dto.getNome()).descricao(dto.getDescricao())
            .condicaoTipo(dto.getCondicaoTipo()).condicaoValor(dto.getCondicaoValor())
            .acaoTipo(dto.getAcaoTipo()).acaoConfig(dto.getAcaoConfig() != null ? dto.getAcaoConfig() : "{}")
            .ativa(dto.getAtiva() != null ? dto.getAtiva() : true)
            .prioridade(dto.getPrioridade() != null ? dto.getPrioridade() : 0)
            .build();
        log.info("Criando regra '{}' para utilizador {}", dto.getNome(), utilizadorId);
        return repository.save(r);
    }

    @Override
    public RegraAutomacao atualizar(Long id, RegraDTO dto, Long utilizadorId) {
        RegraAutomacao r = buscarPorId(id);
        if (!r.getUtilizador().getId().equals(utilizadorId))
            throw new SecurityException("Sem permissão para editar esta regra.");
        r.setNome(dto.getNome()); r.setDescricao(dto.getDescricao());
        r.setCondicaoTipo(dto.getCondicaoTipo()); r.setCondicaoValor(dto.getCondicaoValor());
        r.setAcaoTipo(dto.getAcaoTipo());
        if (dto.getAcaoConfig() != null) r.setAcaoConfig(dto.getAcaoConfig());
        if (dto.getAtiva() != null) r.setAtiva(dto.getAtiva());
        if (dto.getPrioridade() != null) r.setPrioridade(dto.getPrioridade());
        return repository.save(r);
    }

    @Override
    public void excluir(Long id, Long utilizadorId) {
        RegraAutomacao r = buscarPorId(id);
        if (!r.getUtilizador().getId().equals(utilizadorId))
            throw new SecurityException("Sem permissão para excluir esta regra.");
        repository.delete(r);
    }

    @Override @Transactional(readOnly = true)
    public RegraAutomacao buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Regra", id));
    }

    @Override @Transactional(readOnly = true)
    public List<RegraAutomacao> listarPorUtilizador(Long utilizadorId) {
        return repository.findByUtilizadorIdOrderByPrioridadeDescCriadoEmDesc(utilizadorId);
    }

    @Override @Transactional(readOnly = true)
    public List<RegraAutomacao> listarAtivasPorUtilizador(Long utilizadorId) {
        return repository.findRegrasAtivasOrdenadas(utilizadorId);
    }

    @Override @Transactional(readOnly = true)
    public List<RegraAutomacao> pesquisar(Long utilizadorId, String query) {
        return repository.pesquisar(utilizadorId, query.toLowerCase());
    }

    @Override
    public void alternarAtiva(Long id, Long utilizadorId) {
        RegraAutomacao r = buscarPorId(id);
        if (!r.getUtilizador().getId().equals(utilizadorId))
            throw new SecurityException("Sem permissão.");
        r.setAtiva(!r.getAtiva());
        repository.save(r);
    }
}
