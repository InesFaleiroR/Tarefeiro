package com.tarefeiro.api.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarefeiro.api.dto.ApiResponse;
import com.tarefeiro.api.dto.EventoApiDTO;
import com.tarefeiro.api.dto.WebhookPayload;
import com.tarefeiro.dto.EventoDTO;
import com.tarefeiro.model.Evento;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.service.EventoService;
import com.tarefeiro.service.UtilizadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final EventoService eventoService;
    private final UtilizadorService utilizadorService;
    private final ObjectMapper objectMapper;

    @Value("${tarefeiro.webhook.secret:webhook-secret-alterar-em-producao}")
    private String webhookSecret;

    /**
     * POST /api/v1/webhooks/evento
     * Recebe evento via Bearer JWT (utilizador autenticado).
     */
    @PostMapping("/evento")
    public ResponseEntity<ApiResponse<EventoApiDTO>> receberEvento(
            @Valid @RequestBody WebhookPayload payload,
            @AuthenticationPrincipal Utilizador utilizador) {

        if (utilizador == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.erro("Autenticacao necessaria. Usa Bearer JWT ou webhook secret."));
        }
        return processarEvento(payload, utilizador);
    }

    /**
     * POST /api/v1/webhooks/externo
     * Recebe eventos de sistemas externos via X-Webhook-Secret (sem JWT).
     */
    @PostMapping("/externo")
    public ResponseEntity<ApiResponse<EventoApiDTO>> receberExterno(
            @RequestHeader(value = "X-Webhook-Secret",     required = false) String secret,
            @RequestHeader(value = "X-Webhook-User-Email", required = false) String userEmail,
            @RequestHeader(value = "X-Webhook-Source", defaultValue = "externo") String fonte,
            @Valid @RequestBody WebhookPayload payload) {

        if (secret == null || !webhookSecret.equals(secret)) {
            log.warn("Webhook com secret invalido. Source: {}", fonte);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.erro("Webhook secret invalido ou em falta."));
        }
        if (userEmail == null || userEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.erro("Header X-Webhook-User-Email e obrigatorio."));
        }

        Utilizador utilizador;
        try {
            utilizador = utilizadorService.buscarPorEmail(userEmail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.erro("Utilizador nao encontrado: " + userEmail));
        }

        if (payload.getFonte() == null) {
            payload.setFonte(fonte);
        }

        log.info("Webhook externo de '{}' para '{}'", fonte, userEmail);
        return processarEvento(payload, utilizador);
    }

    /**
     * POST /api/v1/webhooks/gmail
     * Endpoint especializado para integracoes Gmail.
     */
    @PostMapping("/gmail")
    public ResponseEntity<ApiResponse<EventoApiDTO>> receberGmail(
            @RequestHeader(value = "X-Webhook-Secret",     required = false) String secret,
            @RequestHeader(value = "X-Webhook-User-Email") String userEmail,
            @RequestBody Map<String, Object> body) {

        if (!webhookSecret.equals(secret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.erro("Secret invalido."));
        }

        WebhookPayload payload = new WebhookPayload();
        payload.setTipo("EMAIL_RECEBIDO");
        payload.setTitulo(String.valueOf(body.getOrDefault("subject", "Email sem assunto")));
        payload.setFonte("Gmail");
        payload.setDescricao(String.valueOf(body.getOrDefault("snippet", "")));

        try {
            payload.getDados().put("raw", objectMapper.writeValueAsString(body));
        } catch (Exception ignored) {
            // ignora erro de serializa cao
        }

        Utilizador u = utilizadorService.buscarPorEmail(userEmail);
        return processarEvento(payload, u);
    }

    /**
     * POST /api/v1/webhooks/link
     * Endpoint especializado para partilha de links (ex: extensao browser).
     */
    @PostMapping("/link")
    public ResponseEntity<ApiResponse<EventoApiDTO>> receberLink(
            @RequestHeader(value = "X-Webhook-Secret",     required = false) String secret,
            @RequestHeader(value = "X-Webhook-User-Email") String userEmail,
            @RequestBody Map<String, String> body) {

        if (!webhookSecret.equals(secret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.erro("Secret invalido."));
        }

        WebhookPayload payload = new WebhookPayload();
        payload.setTipo("LINK_PARTILHADO");
        payload.setTitulo(body.getOrDefault("titulo", body.getOrDefault("url", "Link partilhado")));
        payload.setFonte("Browser Extension");
        payload.getDados().put("url",    body.get("url"));
        payload.getDados().put("titulo", body.get("titulo"));

        Utilizador u = utilizadorService.buscarPorEmail(userEmail);
        return processarEvento(payload, u);
    }

    /**
     * GET /api/v1/webhooks/health
     * Endpoint publico para verificar se os webhooks estao ativos.
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> info = new HashMap<>();
        info.put("status",    "ok");
        info.put("versao",    "1.0");
        info.put("endpoint1", "POST /api/v1/webhooks/evento (Bearer JWT)");
        info.put("endpoint2", "POST /api/v1/webhooks/externo (X-Webhook-Secret)");
        info.put("endpoint3", "POST /api/v1/webhooks/gmail");
        info.put("endpoint4", "POST /api/v1/webhooks/link");
        return ResponseEntity.ok(ApiResponse.ok(info, "Webhook ativo!"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // privado
    // ─────────────────────────────────────────────────────────────────────────

    private ResponseEntity<ApiResponse<EventoApiDTO>> processarEvento(
            WebhookPayload payload, Utilizador utilizador) {

        EventoDTO dto = new EventoDTO();
        dto.setTipo(payload.getTipo());
        dto.setTitulo(payload.getTitulo());
        dto.setDescricao(payload.getDescricao());
        dto.setFonte(payload.getFonte() != null ? payload.getFonte() : "webhook");

        try {
            dto.setPayload(
                payload.getDados().isEmpty()
                    ? "{}"
                    : objectMapper.writeValueAsString(payload.getDados())
            );
        } catch (Exception e) {
            dto.setPayload("{}");
        }

        Evento evento = eventoService.criar(dto, utilizador.getId());
        log.info("Evento '{}' criado via webhook para '{}'",
            payload.getTipo(), utilizador.getEmail());

        EventoApiDTO resposta = EventoApiDTO.builder()
            .id(evento.getId())
            .tipo(evento.getTipo())
            .titulo(evento.getTitulo())
            .descricao(evento.getDescricao())
            .fonte(evento.getFonte())
            .processado(evento.getProcessado())
            .criadoEm(evento.getCriadoEm())
            .build();

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(resposta, "Evento recebido e em fila de processamento!"));
    }
}
