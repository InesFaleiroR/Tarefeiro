package com.tarefeiro.controller;

import com.tarefeiro.model.Utilizador;
import com.tarefeiro.service.ExecucaoAcaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/execucoes")
@RequiredArgsConstructor
public class ExecucaoController {
    private final ExecucaoAcaoService execucaoService;

    @GetMapping
    public String listar(@AuthenticationPrincipal Utilizador u,
                         @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("execucoes", execucaoService.listarPorUtilizador(u.getId(), PageRequest.of(page, 20)));
        model.addAttribute("page", page);
        model.addAttribute("utilizador", u);
        return "execucoes/listaexecucoes";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, @AuthenticationPrincipal Utilizador u, Model model) {
        model.addAttribute("execucao", execucaoService.buscarPorId(id));
        model.addAttribute("utilizador", u);
        return "execucoes/detalheexecucao";
    }
}
