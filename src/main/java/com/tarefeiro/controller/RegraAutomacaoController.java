package com.tarefeiro.controller;

import com.tarefeiro.dto.RegraDTO;
import com.tarefeiro.enums.TipoAcao;
import com.tarefeiro.enums.TipoCondicao;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.service.RegraAutomacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/regras")
@RequiredArgsConstructor
public class RegraAutomacaoController {
    private final RegraAutomacaoService regraService;

    private void adicionarEnums(Model model) {
        model.addAttribute("tiposCondicao", TipoCondicao.values());
        model.addAttribute("tiposAcao", TipoAcao.values());
    }

    @GetMapping
    public String listar(@AuthenticationPrincipal Utilizador u,
                         @RequestParam(required = false) String q, Model model) {
        var regras = (q != null && !q.isBlank()) ? regraService.pesquisar(u.getId(), q) : regraService.listarPorUtilizador(u.getId());
        model.addAttribute("regras", regras);
        model.addAttribute("q", q);
        model.addAttribute("utilizador", u);
        return "regras/listaregras";
    }

    @GetMapping("/nova")
    public String novaForm(@AuthenticationPrincipal Utilizador u, Model model) {
        model.addAttribute("regraDTO", new RegraDTO());
        model.addAttribute("utilizador", u);
        adicionarEnums(model);
        return "regras/novaregra";
    }

    @PostMapping("/nova")
    public String criar(@AuthenticationPrincipal Utilizador u,
                        @Valid @ModelAttribute RegraDTO dto, BindingResult br,
                        Model model, RedirectAttributes ra) {
        if (br.hasErrors()) { adicionarEnums(model); return "regras/novaregra"; }
        regraService.criar(dto, u.getId());
        ra.addFlashAttribute("sucesso", "Regra criada com sucesso!");
        return "redirect:/regras";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, @AuthenticationPrincipal Utilizador u, Model model) {
        var regra = regraService.buscarPorId(id);
        RegraDTO dto = RegraDTO.builder().id(regra.getId()).nome(regra.getNome())
            .descricao(regra.getDescricao()).condicaoTipo(regra.getCondicaoTipo())
            .condicaoValor(regra.getCondicaoValor()).acaoTipo(regra.getAcaoTipo())
            .acaoConfig(regra.getAcaoConfig()).ativa(regra.getAtiva()).prioridade(regra.getPrioridade()).build();
        model.addAttribute("regraDTO", dto);
        model.addAttribute("regraId", id);
        model.addAttribute("utilizador", u);
        adicionarEnums(model);
        return "regras/editarregra";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable Long id, @AuthenticationPrincipal Utilizador u,
                         @Valid @ModelAttribute RegraDTO dto, BindingResult br,
                         Model model, RedirectAttributes ra) {
        if (br.hasErrors()) { adicionarEnums(model); return "regras/editarregra"; }
        regraService.atualizar(id, dto, u.getId());
        ra.addFlashAttribute("sucesso", "Regra atualizada com sucesso!");
        return "redirect:/regras";
    }

    @PostMapping("/{id}/alternar")
    public String alternar(@PathVariable Long id, @AuthenticationPrincipal Utilizador u, RedirectAttributes ra) {
        regraService.alternarAtiva(id, u.getId());
        ra.addFlashAttribute("sucesso", "Estado da regra alterado.");
        return "redirect:/regras";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, @AuthenticationPrincipal Utilizador u, RedirectAttributes ra) {
        regraService.excluir(id, u.getId());
        ra.addFlashAttribute("sucesso", "Regra eliminada com sucesso.");
        return "redirect:/regras";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, @AuthenticationPrincipal Utilizador u, Model model) {
        model.addAttribute("regra", regraService.buscarPorId(id));
        model.addAttribute("utilizador", u);
        return "regras/detalheregra";
    }
}
