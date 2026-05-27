package com.tarefeiro.api.controller;

import com.tarefeiro.api.dto.ApiResponse;
import com.tarefeiro.dto.DashboardDTO;
import com.tarefeiro.model.Notificacao;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.service.ExecucaoAcaoService;
import com.tarefeiro.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final ExecucaoAcaoService execucaoService;
    private final NotificacaoService notificacaoService;

    /** GET /api/v1/dashboard — estatisticas completas */
    @GetMapping
    public ResponseEntity<ApiResponse<DashboardDTO>> dashboard(
            @AuthenticationPrincipal Utilizador u) {

        DashboardDTO dto = execucaoService.obterDashboard(u.getId());
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    /** GET /api/v1/dashboard/notificacoes — nao lidas */
    @GetMapping("/notificacoes")
    public ResponseEntity<ApiResponse<List<Notificacao>>> notificacoes(
            @AuthenticationPrincipal Utilizador u) {

        List<Notificacao> lista = notificacaoService.listarNaoLidas(u.getId());
        return ResponseEntity.ok(ApiResponse.lista(lista, lista.size()));
    }

    /** PATCH /api/v1/dashboard/notificacoes/ler-todas */
    @PatchMapping("/notificacoes/ler-todas")
    public ResponseEntity<ApiResponse<Void>> lerTodas(
            @AuthenticationPrincipal Utilizador u) {

        notificacaoService.marcarTodasLidas(u.getId());
        return ResponseEntity.ok(ApiResponse.ok(null, "Todas as notificacoes marcadas como lidas."));
    }

    /** GET /api/v1/dashboard/health — ping do sistema */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> info = new HashMap<>();
        info.put("status", "ok");
        info.put("versao", "1.0.0");
        info.put("nome", "O Tarefeiro");
        info.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(ApiResponse.ok(info));
    }
}
