package com.tarefeiro.controller;

import com.tarefeiro.model.Utilizador;
import com.tarefeiro.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {
    private final NotificacaoService notificacaoService;

    @PostMapping("/{id}/ler")
    public String marcarLida(@PathVariable Long id, @AuthenticationPrincipal Utilizador u, RedirectAttributes ra) {
        notificacaoService.marcarLida(id);
        return "redirect:/dashboard";
    }

    @PostMapping("/ler-todas")
    public String marcarTodasLidas(@AuthenticationPrincipal Utilizador u, RedirectAttributes ra) {
        notificacaoService.marcarTodasLidas(u.getId());
        ra.addFlashAttribute("sucesso", "Todas as notificações marcadas como lidas.");
        return "redirect:/dashboard";
    }

    @PostMapping("/limpar")
    public String limpar(@AuthenticationPrincipal Utilizador u, RedirectAttributes ra) {
        notificacaoService.limparLidas(u.getId());
        ra.addFlashAttribute("sucesso", "Notificações lidas eliminadas.");
        return "redirect:/dashboard";
    }
}
