package com.tarefeiro.automation;

import com.tarefeiro.enums.EstadoExecucao;
import com.tarefeiro.model.Evento;
import com.tarefeiro.model.ExecucaoAcao;
import com.tarefeiro.model.RegraAutomacao;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.repository.ExecucaoAcaoRepository;
import com.tarefeiro.repository.RegraAutomacaoRepository;
import com.tarefeiro.repository.UtilizadorRepository;
import com.tarefeiro.service.EventoService;
import com.tarefeiro.service.NotificacaoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MotorAutomacao {

    private final EventoService eventoService;
    private final RegraAutomacaoRepository regraRepository;
    private final ExecucaoAcaoRepository execucaoRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final RegraExecutor regraExecutor;
    private final NotificacaoService notificacaoService;

    @Scheduled(
            fixedDelayString = "${tarefeiro.automacao.intervalo-verificacao-ms:60000}"
    )
    @Transactional
    public void processarEventosPendentes() {

        try {

            log.debug("Motor de automação: verificando eventos pendentes...");

            List<Utilizador> utilizadores = utilizadorRepository.findAllAtivos();

            if (utilizadores.isEmpty()) {
                log.debug("Nenhum utilizador ativo encontrado.");
                return;
            }

            for (Utilizador utilizador : utilizadores) {

                try {

                    processarEventosPorUtilizador(utilizador);

                } catch (Exception e) {

                    log.error(
                            "Erro ao processar utilizador {}: {}",
                            utilizador.getId(),
                            e.getMessage(),
                            e
                    );
                }
            }

        } catch (Exception e) {

            log.error(
                    "Erro geral no motor de automação: {}",
                    e.getMessage(),
                    e
            );
        }
    }

    private void processarEventosPorUtilizador(Utilizador utilizador) {

        List<Evento> eventosPendentes =
                eventoService.listarNaoProcessados(utilizador.getId());

        if (eventosPendentes.isEmpty()) {

            log.debug(
                    "Nenhum evento pendente para o utilizador {}",
                    utilizador.getId()
            );

            return;
        }

        List<RegraAutomacao> regrasAtivas =
                regraRepository.findRegrasAtivasOrdenadas(utilizador.getId());

        if (regrasAtivas.isEmpty()) {

            log.debug(
                    "Nenhuma regra ativa para o utilizador {}",
                    utilizador.getId()
            );

            return;
        }

        for (Evento evento : eventosPendentes) {

            for (RegraAutomacao regra : regrasAtivas) {

                if (condicaoCorresponde(regra, evento)) {

                    executarAcao(regra, evento, utilizador);

                    eventoService.marcarProcessado(evento.getId());

                    break;
                }
            }
        }
    }

    private boolean condicaoCorresponde(
            RegraAutomacao regra,
            Evento evento
    ) {

        return switch (regra.getCondicaoTipo()) {

            case "EMAIL_ASSUNTO_CONTEM" -> {

                String payload =
                        evento.getPayload() != null
                                ? evento.getPayload().toLowerCase()
                                : "";

                String[] termos =
                        regra.getCondicaoValor().toLowerCase().split(",");

                boolean matched = false;

                for (String termo : termos) {

                    if (payload.contains(termo.trim())) {

                        matched = true;
                        break;
                    }
                }

                yield matched &&
                        "EMAIL_RECEBIDO".equals(evento.getTipo());
            }

            case "LINK_DOMINIO_CONTEM" -> {

                String payload =
                        evento.getPayload() != null
                                ? evento.getPayload().toLowerCase()
                                : "";

                String[] dominios =
                        regra.getCondicaoValor().toLowerCase().split(",");

                boolean matched = false;

                for (String dominio : dominios) {

                    if (payload.contains(dominio.trim())) {

                        matched = true;
                        break;
                    }
                }

                yield matched &&
                        "LINK_PARTILHADO".equals(evento.getTipo());
            }

            case "EVENTO_TIPO" ->
                    regra.getCondicaoValor()
                            .equalsIgnoreCase(evento.getTipo());

            case "SEMPRE" -> true;

            default -> false;
        };
    }

    private void executarAcao(
            RegraAutomacao regra,
            Evento evento,
            Utilizador utilizador
    ) {

        Map<String, Object> metadata = new HashMap<>();

        metadata.put("regraId", regra.getId());
        metadata.put("regraNome", regra.getNome());
        metadata.put("eventoId", evento.getId());
        metadata.put("eventoTipo", evento.getTipo());
        metadata.put("utilizadorId", utilizador.getId());
        metadata.put("executadoEm", LocalDateTime.now().toString());
        metadata.put("estado", "EXECUTANDO");

        ExecucaoAcao execucao = ExecucaoAcao.builder()
                .regra(regra)
                .evento(evento)
                .utilizador(utilizador)
                .estado(EstadoExecucao.EXECUTANDO)
                .metadata(metadata)
                .build();

        execucaoRepository.save(execucao);

        long inicio = System.currentTimeMillis();

        try {

            String resultado =
                    regraExecutor.executar(regra, evento);

            long duracao =
                    System.currentTimeMillis() - inicio;

            execucao.marcarSucesso(resultado, duracao);

            execucao.getMetadata().put("resultado", resultado);
            execucao.getMetadata().put("duracaoMs", duracao);

            regra.incrementarExecucoes();

            regraRepository.save(regra);

            log.info(
                    "Regra '{}' executada com sucesso para utilizador {}",
                    regra.getNome(),
                    utilizador.getId()
            );

        } catch (Exception e) {

            execucao.marcarFalha(e.getMessage());

            execucao.getMetadata().put("erro", e.getMessage());

            log.error(
                    "Falha na execução da regra '{}': {}",
                    regra.getNome(),
                    e.getMessage(),
                    e
            );

            notificacaoService.criar(
                    utilizador.getId(),
                    "Falha na automação: " + regra.getNome(),
                    "A regra falhou: " + e.getMessage(),
                    "ERRO"
            );
        }

        execucaoRepository.save(execucao);
    }
}