package com.tarefeiro.controller;

import com.tarefeiro.model.Utilizador;
import com.tarefeiro.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final ExecucaoAcaoService execucaoService;
    private final NotificacaoService notificacaoService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal Utilizador utilizador, Model model) {
        model.addAttribute("dashboard", execucaoService.obterDashboard(utilizador.getId()));
        model.addAttribute("notificacoes", notificacaoService.listarNaoLidas(utilizador.getId()));
        model.addAttribute("utilizador", utilizador);
        return "dashboard/dashboard";
    }
}
