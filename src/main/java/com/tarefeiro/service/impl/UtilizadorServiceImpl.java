package com.tarefeiro.service.impl;

import com.tarefeiro.dto.UtilizadorDTO;
import com.tarefeiro.exception.ResourceNotFoundException;
import com.tarefeiro.model.Utilizador;
import com.tarefeiro.repository.UtilizadorRepository;
import com.tarefeiro.service.UtilizadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j @Transactional
public class UtilizadorServiceImpl implements UtilizadorService {
    private final UtilizadorRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Utilizador registar(UtilizadorDTO dto) {
        if (repository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("Já existe um utilizador com este email.");
        Utilizador u = Utilizador.builder()
            .nome(dto.getNome()).email(dto.getEmail())
            .senha(passwordEncoder.encode(dto.getSenha()))
            .role(dto.getRole() != null ? dto.getRole() : "USER")
            .ativo(true).build();
        log.info("Registando utilizador: {}", dto.getEmail());
        return repository.save(u);
    }

    @Override
    public Utilizador atualizar(Long id, UtilizadorDTO dto) {
        Utilizador u = buscarPorId(id);
        u.setNome(dto.getNome());
        if (dto.getSenha() != null && !dto.getSenha().isBlank())
            u.setSenha(passwordEncoder.encode(dto.getSenha()));
        if (dto.getFotoPerfil() != null) u.setFotoPerfil(dto.getFotoPerfil());
        return repository.save(u);
    }

    @Override
    public void desativar(Long id) {
        Utilizador u = buscarPorId(id);
        u.setAtivo(false);
        repository.save(u);
    }

    @Override @Transactional(readOnly = true)
    public Utilizador buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Utilizador", id));
    }

    @Override @Transactional(readOnly = true)
    public Utilizador buscarPorEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Utilizador com email " + email));
    }

    @Override @Transactional(readOnly = true)
    public List<Utilizador> listarTodos() { return repository.findAllAtivos(); }

    @Override @Transactional(readOnly = true)
    public boolean emailExiste(String email) { return repository.existsByEmail(email); }

    @Override
    public void atualizarUltimoAcesso(String email) {
        repository.findByEmail(email).ifPresent(u -> { u.setUltimoAcesso(LocalDateTime.now()); repository.save(u); });
    }
}
