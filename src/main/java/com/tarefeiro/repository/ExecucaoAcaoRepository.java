package com.tarefeiro.repository;

import com.tarefeiro.enums.EstadoExecucao;
import com.tarefeiro.model.ExecucaoAcao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExecucaoAcaoRepository extends JpaRepository<ExecucaoAcao, Long> {
    Optional<ExecucaoAcao> findByUuid(UUID uuid);
    Page<ExecucaoAcao> findByUtilizadorIdOrderByIniciadoEmDesc(Long utilizadorId, Pageable pageable);

    // Corrigido de findByRegraidAndEstado para findByRegraIdAndEstado
    List<ExecucaoAcao> findByRegraIdAndEstado(Long regraId, EstadoExecucao estado);

    long countByUtilizadorIdAndEstado(Long utilizadorId, EstadoExecucao estado);

    @Query("SELECT e FROM ExecucaoAcao e WHERE e.utilizador.id = :uid ORDER BY e.iniciadoEm DESC")
    List<ExecucaoAcao> findRecentes(@Param("uid") Long utilizadorId, Pageable pageable);
}