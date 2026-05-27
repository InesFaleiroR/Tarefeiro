package com.tarefeiro.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarefeiro.dto.EventoDTO;
import com.tarefeiro.exception.ResourceNotFoundException;
import com.tarefeiro.model.Evento;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.repository.EventoRepository;
import com.tarefeiro.repository.UtilizadorRepository;
import com.tarefeiro.service.EventoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventoServiceImpl implements EventoService {
    private final EventoRepository repository;
    private final UtilizadorRepository utilizadorRepository;
    private final ObjectMapper objectMapper; // Injeção automática do conversor JSON do Spring

    @Override
    public Evento criar(EventoDTO dto, Long utilizadorId) {
        Utilizador u = utilizadorRepository.findById(utilizadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", utilizadorId));

        // 1. Converter a String JSON vinda do DTO num Map estruturado
        Map<String, Object> payloadMap;
        if (dto.getPayload() != null && !dto.getPayload().trim().isEmpty()) {
            try {
                payloadMap = objectMapper.readValue(dto.getPayload(), new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.error("Erro ao converter string de payload para Map. Usando mapa vazio.", e);
                payloadMap = new HashMap<>();
            }
        } else {
            payloadMap = new HashMap<>();
        }

        // 2. Construir a entidade com o tipo correto esperado (Map)
        Evento e = Evento.builder()
                .utilizador(u)
                .tipo(dto.getTipo())
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .fonte(dto.getFonte())
                .payload(payloadMap) // Agora passa o Map corretamente configurado
                .processado(false)
                .build();

        log.info("Criando evento '{}' para utilizador {}", dto.getTitulo(), utilizadorId);
        return repository.save(e);
    }

    @Override @Transactional(readOnly = true)
    public Evento buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }

    @Override @Transactional(readOnly = true)
    public Page<Evento> listarPorUtilizador(Long utilizadorId, Pageable pageable) {
        return repository.findByUtilizadorIdOrderByCriadoEmDesc(utilizadorId, pageable);
    }

    @Override @Transactional(readOnly = true)
    public List<Evento> listarNaoProcessados(Long utilizadorId) {
        return repository.findByUtilizadorIdAndProcessadoFalseOrderByCriadoEmAsc(utilizadorId);
    }

    @Override
    public void marcarProcessado(Long id) {
        Evento e = buscarPorId(id);
        e.setProcessado(true);
        repository.save(e);
    }

    @Override @Transactional(readOnly = true)
    public long contarPendentes(Long utilizadorId) {
        return repository.countByUtilizadorIdAndProcessadoFalse(utilizadorId);
    }
}