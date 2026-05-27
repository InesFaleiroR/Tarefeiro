package com.tarefeiro.api.controller;

import com.tarefeiro.api.dto.ApiResponse;
import com.tarefeiro.api.dto.AuthRequest;
import com.tarefeiro.api.dto.AuthResponse;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.security.JwtUtil;
import com.tarefeiro.service.UtilizadorService;
import com.tarefeiro.service.impl.UtilizadorDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthApiController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UtilizadorDetailsService userDetailsService;
    private final UtilizadorService utilizadorService;

    /**
     * POST /api/v1/auth/login
     * Autentica o utilizador e devolve access + refresh token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest req) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getSenha())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.erro("Credenciais invalidas."));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.erro("Conta desativada."));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getEmail());
        Utilizador utilizador = utilizadorService.buscarPorEmail(req.getEmail());
        utilizadorService.atualizarUltimoAcesso(req.getEmail());

        String accessToken  = jwtUtil.gerarToken(userDetails);
        String refreshToken = jwtUtil.gerarRefreshToken(userDetails);

        AuthResponse resposta = AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tipo("Bearer")
            .expiresIn(86400L)
            .email(utilizador.getEmail())
            .nome(utilizador.getNome())
            .role(utilizador.getRole())
            .build();

        log.info("Login API bem-sucedido para: {}", req.getEmail());
        return ResponseEntity.ok(ApiResponse.ok(resposta, "Login efetuado com sucesso!"));
    }

    /**
     * POST /api/v1/auth/refresh
     * Renova o access token usando o refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestHeader("Authorization") String header) {

        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.erro("Refresh token em falta no header Authorization."));
        }

        String refreshToken = header.substring(7);
        try {
            String email = jwtUtil.extrairEmail(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (!jwtUtil.isTokenValido(refreshToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.erro("Refresh token invalido ou expirado."));
            }

            String newAccessToken = jwtUtil.gerarToken(userDetails);
            Utilizador u = utilizadorService.buscarPorEmail(email);

            AuthResponse resposta = AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tipo("Bearer")
                .expiresIn(86400L)
                .email(u.getEmail())
                .nome(u.getNome())
                .role(u.getRole())
                .build();

            return ResponseEntity.ok(ApiResponse.ok(resposta, "Token renovado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.erro("Refresh token invalido."));
        }
    }

    /**
     * GET /api/v1/auth/me
     * Devolve dados do utilizador autenticado.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, String>>> me(
            @AuthenticationPrincipal Utilizador u) {

        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.erro("Nao autenticado."));
        }

        Map<String, String> dados = new HashMap<>();
        dados.put("id", String.valueOf(u.getId()));
        dados.put("nome", u.getNome());
        dados.put("email", u.getEmail());
        dados.put("role", u.getRole());

        return ResponseEntity.ok(ApiResponse.ok(dados));
    }
}
