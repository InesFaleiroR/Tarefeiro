package com.tarefeiro.controller;

import com.tarefeiro.ai.InterpretadorIAService;
import com.tarefeiro.dto.IARequestDTO;
import com.tarefeiro.model.Utilizador;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ia")
@RequiredArgsConstructor
public class IAController {
    private final InterpretadorIAService iaService;

    @GetMapping
    public String pagina(@AuthenticationPrincipal Utilizador u, Model model) {
        model.addAttribute("iaRequest", new IARequestDTO());
        model.addAttribute("utilizador", u);
        return "ia/ia";
    }

    @PostMapping("/processar")
    public String processar(@AuthenticationPrincipal Utilizador u,
                            @Valid @ModelAttribute IARequestDTO dto, BindingResult br,
                            Model model) {
        model.addAttribute("utilizador", u);
        model.addAttribute("iaRequest", dto);
        if (br.hasErrors()) return "ia/ia";
        String resultado = switch (dto.getTipo() != null ? dto.getTipo() : "RESUMIR") {
            case "RESUMIR"        -> iaService.resumirTexto(dto.getTexto());
            case "CHECKLIST"      -> iaService.criarChecklist(dto.getTexto());
            case "INTERPRETAR"    -> iaService.interpretarIntencao(dto.getTexto());
            case "CRIAR_REGRA"    -> iaService.sugerirRegraAutomacao(dto.getTexto());
            default               -> iaService.resumirTexto(dto.getTexto());
        };
        model.addAttribute("resultado", resultado);
        return "ia/ia";
    }
}
