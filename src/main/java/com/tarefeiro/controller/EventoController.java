package com.tarefeiro.controller;

import com.tarefeiro.dto.EventoDTO;
import com.tarefeiro.enums.TipoEvento;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {
    private final EventoService eventoService;

    @GetMapping
    public String listar(@AuthenticationPrincipal Utilizador u,
                         @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("eventos", eventoService.listarPorUtilizador(u.getId(), PageRequest.of(page, 15)));
        model.addAttribute("page", page);
        model.addAttribute("utilizador", u);
        return "eventos/listaeventos";
    }

    @GetMapping("/novo")
    public String novoForm(@AuthenticationPrincipal Utilizador u, Model model) {
        model.addAttribute("eventoDTO", new EventoDTO());
        model.addAttribute("tiposEvento", TipoEvento.values());
        model.addAttribute("utilizador", u);
        return "eventos/novoevento";
    }

    @PostMapping("/novo")
    public String criar(@AuthenticationPrincipal Utilizador u,
                        @Valid @ModelAttribute EventoDTO dto, BindingResult br,
                        Model model, RedirectAttributes ra) {
        if (br.hasErrors()) { model.addAttribute("tiposEvento", TipoEvento.values()); return "eventos/novoevento"; }
        eventoService.criar(dto, u.getId());
        ra.addFlashAttribute("sucesso", "Evento criado e em fila de processamento!");
        return "redirect:/eventos";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, @AuthenticationPrincipal Utilizador u, Model model) {
        model.addAttribute("evento", eventoService.buscarPorId(id));
        model.addAttribute("utilizador", u);
        return "eventos/detalheevento";
    }
}
