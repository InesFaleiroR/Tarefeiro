package com.tarefeiro.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarefeiro.api.dto.ApiResponse;
import com.tarefeiro.api.dto.EventoApiDTO;
import com.tarefeiro.dto.EventoDTO;
import com.tarefeiro.model.Evento;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
@Slf4j
public class EventoApiController {

    private final EventoService eventoService;
    private final ObjectMapper objectMapper; // Adicionado para converter o Map para String no toDTO

    /** GET /api/v1/eventos?page=0&size=20 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventoApiDTO>>> listar(
            @AuthenticationPrincipal Utilizador u,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Evento> pg = eventoService.listarPorUtilizador(u.getId(), PageRequest.of(page, size));
        List<EventoApiDTO> lista = pg.getContent().stream().map(this::toDTO).toList();

        return ResponseEntity.ok(ApiResponse.<List<EventoApiDTO>>builder()
                .sucesso(true)
                .dados(lista)
                .total((int) pg.getTotalElements())
                .build());
    }

    /** GET /api/v1/eventos/pendentes */
    @GetMapping("/pendentes")
    public ResponseEntity<ApiResponse<List<EventoApiDTO>>> pendentes(
            @AuthenticationPrincipal Utilizador u) {

        List<EventoApiDTO> lista = eventoService.listarNaoProcessados(u.getId())
                .stream().map(this::toDTO).toList();
        return ResponseEntity.ok(ApiResponse.lista(lista, lista.size()));
    }

    /** GET /api/v1/eventos/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventoApiDTO>> detalhe(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilizador u) {

        Evento e = eventoService.buscarPorId(id);
        if (!e.getUtilizador().getId().equals(u.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.erro("Acesso negado."));
        }
        return ResponseEntity.ok(ApiResponse.ok(toDTO(e)));
    }

    /** POST /api/v1/eventos — criar evento manual */
    @PostMapping
    public ResponseEntity<ApiResponse<EventoApiDTO>> criar(
            @Valid @RequestBody EventoDTO dto,
            @AuthenticationPrincipal Utilizador u) {

        Evento novo = eventoService.criar(dto, u.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(toDTO(novo), "Evento criado e em fila de processamento!"));
    }

    /** GET /api/v1/eventos/stats */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> stats(
            @AuthenticationPrincipal Utilizador u) {

        long pendentes = eventoService.contarPendentes(u.getId());
        Map<String, Long> dados = new HashMap<>();
        dados.put("pendentes", pendentes);
        return ResponseEntity.ok(ApiResponse.ok(dados));
    }

    private EventoApiDTO toDTO(Evento e) {
        String payloadJson = "{}";
        if (e.getPayload() != null) {
            try {
                // Correção aplicada: Transforma o Map<String, Object> de volta numa String JSON válida
                payloadJson = objectMapper.writeValueAsString(e.getPayload());
            } catch (Exception ex) {
                log.error("Erro ao converter payload Map para String JSON no DTO", ex);
            }
        }

        return EventoApiDTO.builder()
                .id(e.getId())
                .tipo(e.getTipo())
                .titulo(e.getTitulo())
                .descricao(e.getDescricao())
                .fonte(e.getFonte())
                .payload(payloadJson) // Agora passa a String esperada pelo EventoApiDTO
                .processado(e.getProcessado())
                .criadoEm(e.getCriadoEm())
                .build();
    }
}