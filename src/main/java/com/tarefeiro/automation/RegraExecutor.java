package com.tarefeiro.automation;

import com.tarefeiro.ai.InterpretadorIAService;
import com.tarefeiro.model.Evento;
import com.tarefeiro.model.RegraAutomacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class RegraExecutor {
    private final InterpretadorIAService iaService;

    public String executar(RegraAutomacao regra, Evento evento) {
        log.info("Executando ação '{}' para regra '{}'", regra.getAcaoTipo(), regra.getNome());
        return switch (regra.getAcaoTipo()) {
            case "ARQUIVAR_EMAIL" -> executarArquivarEmail(regra, evento);
            case "RESUMIR_LINK"   -> executarResumirLink(regra, evento);
            case "CRIAR_CHECKLIST" -> executarCriarChecklist(regra, evento);
            case "ENVIAR_NOTIFICACAO" -> executarNotificacao(regra, evento);
            case "CRIAR_TAREFA"   -> executarCriarTarefa(regra, evento);
            default -> throw new IllegalArgumentException("Ação desconhecida: " + regra.getAcaoTipo());
        };
    }

    private String executarArquivarEmail(RegraAutomacao regra, Evento evento) {
        String pasta = extrairConfig(regra.getAcaoConfig(), "pasta", "Arquivados");
        log.info("Arquivando email '{}' na pasta '{}'", evento.getTitulo(), pasta);
        return "Email arquivado na pasta: " + pasta;
    }

    private String executarResumirLink(RegraAutomacao regra, Evento evento) {
        String url = extrairPayload(evento.getPayload(), "url", "");
        String titulo = extrairPayload(evento.getPayload(), "titulo", evento.getTitulo());
        String resumo = iaService.resumirLink(url, titulo);
        return "Resumo gerado: " + resumo;
    }

    private String executarCriarChecklist(RegraAutomacao regra, Evento evento) {
        String checklist = iaService.criarChecklist(evento.getTitulo() + " - " + evento.getDescricao());
        return "Checklist criada:\n" + checklist;
    }

    private String executarNotificacao(RegraAutomacao regra, Evento evento) {
        return "Notificação enviada para: " + evento.getTitulo();
    }

    private String executarCriarTarefa(RegraAutomacao regra, Evento evento) {
        return "Tarefa criada: " + evento.getTitulo();
    }

    private String extrairConfig(String json, String chave, String padrao) {
        if (json == null) return padrao;
        try {
            int idx = json.indexOf("\"" + chave + "\"");
            if (idx == -1) return padrao;
            int colon = json.indexOf(":", idx) + 1;
            int start = json.indexOf("\"", colon) + 1;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) { return padrao; }
    }

    private String extrairPayload(String json, String chave, String padrao) {
        return extrairConfig(json, chave, padrao);
    }
}
