-- ====================================================
-- O TAREFEIRO - Dados Iniciais (Seed)
-- ====================================================

-- Utilizador administrador padrão (Senha: admin123)
INSERT INTO utilizadores (nome, email, senha, role) VALUES
    ('Administrador', 'admin@tarefeiro.pt', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMqJqhN3SJrECqLH6HqQEbXkAa', 'ADMIN');

-- Utilizador de teste (Senha: user123)
INSERT INTO utilizadores (nome, email, senha, role) VALUES
    ('João Silva', 'joao@exemplo.pt', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMqJqhN3SJrECqLH6HqQEbXkAa', 'USER');

-- Regras de automação de exemplo
INSERT INTO regras_automacao (utilizador_id, nome, descricao, condicao_tipo, condicao_valor, acao_tipo, acao_config, prioridade) VALUES
                                                                                                                                     (2, 'Arquivar emails de newsletters',
                                                                                                                                      'Arquiva automaticamente emails de newsletters e promoções',
                                                                                                                                      'EMAIL_ASSUNTO_CONTEM', 'newsletter,promoção,desconto,oferta',
                                                                                                                                      'ARQUIVAR_EMAIL',
                                                                                                                                      '{"pasta": "Newsletters", "marcar_lido": true}'::jsonb,
                                                                                                                                      10),
                                                                                                                                     (2, 'Resumir artigos de tecnologia',
                                                                                                                                      'Gera resumo automático de links de artigos tech partilhados',
                                                                                                                                      'LINK_DOMINIO_CONTEM', 'medium.com,dev.to,hackernews',
                                                                                                                                      'RESUMIR_LINK',
                                                                                                                                      '{"comprimento": "curto", "idioma": "pt"}'::jsonb,
                                                                                                                                      20),
                                                                                                                                     (2, 'Checklist para reuniões',
                                                                                                                                      'Cria checklist automática quando um evento de reunião é detetado',
                                                                                                                                      'EVENTO_TIPO', 'REUNIAO',
                                                                                                                                      'CRIAR_CHECKLIST',
                                                                                                                                      '{"itens": ["Preparar agenda", "Confirmar participantes", "Testar audiovisual", "Tomar notas"]}'::jsonb,
                                                                                                                                      15),
                                                                                                                                     (2, 'Notificar emails urgentes',
                                                                                                                                      'Envia notificação quando recebe emails marcados como urgentes',
                                                                                                                                      'EMAIL_PRIORIDADE', 'ALTA',
                                                                                                                                      'ENVIAR_NOTIFICACAO',
                                                                                                                                      '{"canal": "app", "som": true}'::jsonb,
                                                                                                                                      5);

-- Eventos de exemplo
INSERT INTO eventos (utilizador_id, tipo, titulo, descricao, fonte, payload) VALUES
                                                                                 (2, 'EMAIL_RECEBIDO', 'Newsletter Semanal Tech', 'Resumo das melhores notícias tech desta semana', 'Gmail', '{"remetente": "news@techdigest.com", "assunto": "Newsletter Semanal Tech"}'::jsonb),
                                                                                 (2, 'LINK_PARTILHADO', 'Artigo: O futuro da IA em 2025', 'Link partilhado para resumir', 'Manual', '{"url": "https://medium.com", "titulo": "O futuro da IA em 2025"}'::jsonb),
                                                                                 (2, 'REUNIAO_AGENDADA', 'Reunião de planeamento Q3', 'Reunião semanal de planeamento', 'Google Calendar', '{"data": "2025-06-15", "hora": "10:00", "participantes": ["maria@empresa.pt", "pedro@empresa.pt"]}'::jsonb);

-- Notificações de exemplo
INSERT INTO notificacoes (utilizador_id, titulo, mensagem, tipo) VALUES
                                                                     (2, 'Bem-vindo ao Tarefeiro!', 'O teu assistente de automação pessoal está pronto. Começa por criar as tuas primeiras regras!', 'INFO'),
                                                                     (2, 'Regra executada com sucesso', 'A regra "Arquivar emails de newsletters" foi executada com sucesso em 3 emails.', 'SUCESSO');