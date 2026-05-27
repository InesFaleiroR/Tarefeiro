package com.tarefeiro.service.impl;

import com.tarefeiro.repository.UtilizadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UtilizadorDetailsService implements UserDetailsService {
    private final UtilizadorRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado: " + email));
    }
}
