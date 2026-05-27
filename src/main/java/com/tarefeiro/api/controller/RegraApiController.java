package com.tarefeiro.api.controller;

import com.tarefeiro.api.dto.ApiResponse;
import com.tarefeiro.api.dto.RegraApiDTO;
import com.tarefeiro.dto.RegraDTO;
import com.tarefeiro.model.RegraAutomacao;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.service.RegraAutomacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regras")
@RequiredArgsConstructor
public class RegraApiController {

    private final RegraAutomacaoService regraService;

    /** GET /api/v1/regras — lista todas */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RegraApiDTO>>> listar(
            @AuthenticationPrincipal Utilizador u) {

        List<RegraApiDTO> regras = regraService.listarPorUtilizador(u.getId())
            .stream().map(this::toDTO).toList();
        return ResponseEntity.ok(ApiResponse.lista(regras, regras.size()));
    }

    /** GET /api/v1/regras/ativas — so ativas */
    @GetMapping("/ativas")
    public ResponseEntity<ApiResponse<List<RegraApiDTO>>> listarAtivas(
            @AuthenticationPrincipal Utilizador u) {

        List<RegraApiDTO> regras = regraService.listarAtivasPorUtilizador(u.getId())
            .stream().map(this::toDTO).toList();
        return ResponseEntity.ok(ApiResponse.lista(regras, regras.size()));
    }

    /** GET /api/v1/regras/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegraApiDTO>> detalhe(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilizador u) {

        RegraAutomacao r = regraService.buscarPorId(id);
        if (!r.getUtilizador().getId().equals(u.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.erro("Acesso negado."));
        }
        return ResponseEntity.ok(ApiResponse.ok(toDTO(r)));
    }

    /** POST /api/v1/regras — criar */
    @PostMapping
    public ResponseEntity<ApiResponse<RegraApiDTO>> criar(
            @Valid @RequestBody RegraDTO dto,
            @AuthenticationPrincipal Utilizador u) {

        RegraAutomacao nova = regraService.criar(dto, u.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(toDTO(nova), "Regra criada com sucesso!"));
    }

    /** PUT /api/v1/regras/{id} — atualizar */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RegraApiDTO>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RegraDTO dto,
            @AuthenticationPrincipal Utilizador u) {

        RegraAutomacao atualizada = regraService.atualizar(id, dto, u.getId());
        return ResponseEntity.ok(ApiResponse.ok(toDTO(atualizada), "Regra atualizada!"));
    }

    /** PATCH /api/v1/regras/{id}/alternar — ativar/desativar */
    @PatchMapping("/{id}/alternar")
    public ResponseEntity<ApiResponse<Void>> alternar(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilizador u) {

        regraService.alternarAtiva(id, u.getId());
        return ResponseEntity.ok(ApiResponse.ok(null, "Estado da regra alterado."));
    }

    /** DELETE /api/v1/regras/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilizador u) {

        regraService.excluir(id, u.getId());
        return ResponseEntity.ok(ApiResponse.ok(null, "Regra eliminada."));
    }

    private RegraApiDTO toDTO(RegraAutomacao r) {
        return RegraApiDTO.builder()
            .id(r.getId())
            .nome(r.getNome())
            .descricao(r.getDescricao())
            .condicaoTipo(r.getCondicaoTipo())
            .condicaoValor(r.getCondicaoValor())
            .acaoTipo(r.getAcaoTipo())
            .acaoConfig(r.getAcaoConfig())
            .ativa(r.getAtiva())
            .prioridade(r.getPrioridade())
            .execucoesTotal(r.getExecucoesTotal())
            .ultimaExecucao(r.getUltimaExecucao())
            .criadoEm(r.getCriadoEm())
            .build();
    }
}
