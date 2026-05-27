package com.tarefeiro.exception;

import com.tarefeiro.api.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /* ── API REST — devolve JSON ─────────────────────────────────────── */

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        if (isApiRequest(req)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.erro(ex.getMessage()));
        }
        return errorView("404", ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public Object handleSecurity(SecurityException ex, HttpServletRequest req) {
        if (isApiRequest(req)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.erro(ex.getMessage()));
        }
        return errorView("403", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining("; "));
        if (isApiRequest(req)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.erro("Validação falhou: " + msg));
        }
        return errorView("400", msg);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegal(IllegalArgumentException ex, HttpServletRequest req) {
        if (isApiRequest(req)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.erro(ex.getMessage()));
        }
        return errorView("400", ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResource(NoResourceFoundException ex, HttpServletRequest req) {
        if (isApiRequest(req)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.erro("Endpoint não encontrado: " + req.getRequestURI()));
        }
        return errorView("404", "Página não encontrada.");
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado em {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        if (isApiRequest(req)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.erro("Erro interno do servidor."));
        }
        return errorView("500", "Ocorreu um erro inesperado.");
    }

    /* ── helpers ─────────────────────────────────────────────────────── */

    private boolean isApiRequest(HttpServletRequest req) {
        String path = req.getRequestURI();
        String accept = req.getHeader("Accept");
        return path.startsWith("/api/") ||
               (accept != null && accept.contains("application/json") && !accept.contains("text/html"));
    }

    private ModelAndView errorView(String code, String mensagem) {
        ModelAndView mav = new ModelAndView("error/" + (code.equals("404") ? "404" : "500"));
        mav.addObject("mensagem", mensagem);
        mav.addObject("codigo", code);
        return mav;
    }
}
