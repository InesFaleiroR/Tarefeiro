package com.tarefeiro.repository;

import com.tarefeiro.model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UtilizadorRepository extends JpaRepository<Utilizador, Long> {
    Optional<Utilizador> findByEmail(String email);
    Optional<Utilizador> findByUuid(UUID uuid);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM Utilizador u WHERE u.ativo = true ORDER BY u.criadoEm DESC")
    java.util.List<Utilizador> findAllAtivos();
}
