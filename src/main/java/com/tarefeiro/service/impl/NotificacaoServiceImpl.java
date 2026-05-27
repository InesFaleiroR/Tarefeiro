package com.tarefeiro.service.impl;

import com.tarefeiro.model.Notificacao;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.repository.NotificacaoRepository;
import com.tarefeiro.repository.UtilizadorRepository;
import com.tarefeiro.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class NotificacaoServiceImpl implements NotificacaoService {
    private final NotificacaoRepository repository;
    private final UtilizadorRepository utilizadorRepository;

    @Override
    public Notificacao criar(Long utilizadorId, String titulo, String mensagem, String tipo) {
        Utilizador u = utilizadorRepository.findById(utilizadorId).orElseThrow();
        return repository.save(Notificacao.builder().utilizador(u).titulo(titulo).mensagem(mensagem).tipo(tipo).build());
    }

    @Override @Transactional(readOnly = true)
    public List<Notificacao> listarPorUtilizador(Long uid) { return repository.findByUtilizadorIdOrderByCriadoEmDesc(uid); }

    @Override @Transactional(readOnly = true)
    public List<Notificacao> listarNaoLidas(Long uid) { return repository.findByUtilizadorIdAndLidaFalseOrderByCriadoEmDesc(uid); }

    @Override
    public void marcarLida(Long id) { repository.findById(id).ifPresent(n -> { n.setLida(true); repository.save(n); }); }

    @Override
    public void marcarTodasLidas(Long uid) { listarNaoLidas(uid).forEach(n -> { n.setLida(true); repository.save(n); }); }

    @Override @Transactional(readOnly = true)
    public long contarNaoLidas(Long uid) { return repository.countByUtilizadorIdAndLidaFalse(uid); }

    @Override
    public void limparLidas(Long uid) { repository.deleteByUtilizadorIdAndLidaTrue(uid); }
}
