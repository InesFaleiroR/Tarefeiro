package com.tarefeiro.controller;

import com.tarefeiro.dto.UtilizadorDTO;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.service.UtilizadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/utilizadores")
@RequiredArgsConstructor
public class UtilizadorController {
    private final UtilizadorService utilizadorService;

    @GetMapping("/registo")
    public String mostrarRegisto(Model model) {
        model.addAttribute("utilizadorDTO", new UtilizadorDTO());
        return "utilizadores/registo";
    }

    @PostMapping("/registo")
    public String registar(@Valid @ModelAttribute UtilizadorDTO dto, BindingResult br,
                           Model model, RedirectAttributes ra) {
        if (br.hasErrors()) { model.addAttribute("utilizadorDTO", dto); return "utilizadores/registo"; }
        if (utilizadorService.emailExiste(dto.getEmail())) {
            model.addAttribute("erro", "Este email já está em uso.");
            return "utilizadores/registo";
        }
        utilizadorService.registar(dto);
        ra.addFlashAttribute("sucesso", "Conta criada com sucesso! Faz login.");
        return "redirect:/login";
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal Utilizador utilizador, Model model) {
        model.addAttribute("utilizador", utilizador);
        model.addAttribute("dto", UtilizadorDTO.builder().nome(utilizador.getNome()).email(utilizador.getEmail()).build());
        return "utilizadores/perfil";
    }

    @PostMapping("/perfil")
    public String atualizarPerfil(@AuthenticationPrincipal Utilizador utilizador,
                                  @Valid @ModelAttribute("dto") UtilizadorDTO dto,
                                  BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) { model.addAttribute("utilizador", utilizador); return "utilizadores/perfil"; }
        utilizadorService.atualizar(utilizador.getId(), dto);
        ra.addFlashAttribute("sucesso", "Perfil atualizado com sucesso!");
        return "redirect:/utilizadores/perfil";
    }
}
