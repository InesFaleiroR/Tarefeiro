package com.tarefeiro.repository;

import com.tarefeiro.model.RegraAutomacao;
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
public interface RegraAutomacaoRepository extends JpaRepository<RegraAutomacao, Long> {
    List<RegraAutomacao> findByUtilizadorIdOrderByPrioridadeDescCriadoEmDesc(Long utilizadorId);
    List<RegraAutomacao> findByUtilizadorIdAndAtivaTrue(Long utilizadorId);
    Optional<RegraAutomacao> findByUuid(UUID uuid);
    Page<RegraAutomacao> findByUtilizadorId(Long utilizadorId, Pageable pageable);
    long countByUtilizadorIdAndAtivaTrue(Long utilizadorId);

    @Query("SELECT r FROM RegraAutomacao r WHERE r.utilizador.id = :uid AND r.ativa = true ORDER BY r.prioridade DESC")
    List<RegraAutomacao> findRegrasAtivasOrdenadas(@Param("uid") Long utilizadorId);

    @Query("SELECT r FROM RegraAutomacao r WHERE r.utilizador.id = :uid AND (LOWER(r.nome) LIKE %:q% OR LOWER(r.descricao) LIKE %:q%)")
    List<RegraAutomacao> pesquisar(@Param("uid") Long utilizadorId, @Param("q") String query);
}
