package com.tarefeiro.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class InterpretadorIAService {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.model:gpt-4o-mini}")
    private String model;

    @Value("${openai.api.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Value("${openai.api.timeout-seconds:30}")
    private int timeoutSeconds;

    @Value("${openai.api.max-tokens:1000}")
    private int maxTokens;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public InterpretadorIAService() {
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    public String resumirTexto(String texto) {
        String prompt = """
            És um assistente especializado em criar resumos concisos e informativos em português europeu.
            Cria um resumo claro e objetivo do seguinte texto, destacando os pontos principais.
            Usa no máximo 3-4 frases.
            
            Texto: %s
            """.formatted(texto);
        return chamarOpenAI(prompt);
    }

    public String resumirLink(String url, String titulo) {
        String prompt = """
            És um assistente que resume artigos e páginas web em português europeu.
            Com base no título e URL fornecidos, cria um resumo informativo do que provavelmente contém este artigo.
            Sê conciso e objetivo. Usa 2-3 frases.
            
            URL: %s
            Título: %s
            """.formatted(url, titulo);
        return chamarOpenAI(prompt);
    }

    public String criarChecklist(String contexto) {
        String prompt = """
            És um assistente de produtividade especializado em criar checklists práticas em português europeu.
            Com base no contexto fornecido, cria uma checklist organizada e acionável.
            Formato: lista de itens com bullet points (-).
            Inclui entre 5 e 10 itens práticos e relevantes.
            
            Contexto: %s
            """.formatted(contexto);
        return chamarOpenAI(prompt);
    }

    public String interpretarIntencao(String texto) {
        String prompt = """
            És um assistente de automação inteligente em português europeu.
            Analisa o seguinte texto e identifica:
            1. Qual é a intenção do utilizador
            2. Que tipo de automação pode ser criada
            3. Sugere uma regra no formato: "SE [condição] → ENTÃO [ação]"
            
            Texto: %s
            """.formatted(texto);
        return chamarOpenAI(prompt);
    }

    public String sugerirRegraAutomacao(String descricao) {
        String prompt = """
            És um especialista em automação de tarefas. Analisa a descrição e sugere uma configuração de regra de automação.
            Responde em JSON com o formato:
            {
              "nome": "Nome da regra",
              "descricao": "Descrição clara",
              "condicao_tipo": "tipo",
              "condicao_valor": "valor",
              "acao_tipo": "tipo",
              "acao_config": {}
            }
            
            Tipos de condição disponíveis: EMAIL_ASSUNTO_CONTEM, EMAIL_REMETENTE_CONTEM, LINK_DOMINIO_CONTEM, EVENTO_TIPO, SEMPRE
            Tipos de ação disponíveis: ARQUIVAR_EMAIL, RESUMIR_LINK, CRIAR_CHECKLIST, ENVIAR_NOTIFICACAO, CRIAR_TAREFA
            
            Descrição: %s
            """.formatted(descricao);
        return chamarOpenAI(prompt);
    }

    private String chamarOpenAI(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Chave OpenAI não configurada. Retornando resposta simulada.");
            return gerarRespostaSimulada(prompt);
        }

        try {
            String requestBody = objectMapper.writeValueAsString(new java.util.HashMap<>() {{
                put("model", model);
                put("max_tokens", maxTokens);
                put("messages", new Object[]{
                    new java.util.HashMap<>() {{ put("role", "user"); put("content", prompt); }}
                });
            }});

            Request request = new Request.Builder()
                .url(baseUrl + "/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Erro na API OpenAI: {}", response.code());
                    return "Erro ao comunicar com o serviço de IA. Tenta novamente.";
                }
                JsonNode json = objectMapper.readTree(response.body().string());
                return json.path("choices").get(0).path("message").path("content").asText();
            }
        } catch (IOException e) {
            log.error("Erro ao chamar OpenAI: {}", e.getMessage());
            return "Erro de comunicação com o serviço de IA.";
        }
    }

    private String gerarRespostaSimulada(String prompt) {
        if (prompt.contains("resumo") || prompt.contains("resumir")) {
            return "Este é um resumo simulado (configure OPENAI_API_KEY para resultados reais). O conteúdo analisa tópicos relevantes da área tecnológica com perspetivas inovadoras.";
        }
        if (prompt.contains("checklist")) {
            return "- Verificar todos os participantes confirmados\n- Preparar a agenda detalhada\n- Testar equipamentos audiovisuais\n- Enviar lembretes 24h antes\n- Preparar materiais de apoio\n- Definir moderador da sessão";
        }
        if (prompt.contains("automação") || prompt.contains("regra")) {
            return """
                {
                  "nome": "Regra Sugerida pela IA",
                  "descricao": "Regra criada automaticamente com base na sua descrição",
                  "condicao_tipo": "EMAIL_ASSUNTO_CONTEM",
                  "condicao_valor": "newsletter",
                  "acao_tipo": "ARQUIVAR_EMAIL",
                  "acao_config": {"pasta": "Newsletters"}
                }""";
        }
        return "Análise IA: Configure a variável OPENAI_API_KEY para obter respostas reais da inteligência artificial.";
    }
}
