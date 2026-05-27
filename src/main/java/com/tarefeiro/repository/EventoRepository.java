package com.tarefeiro.repository;

import com.tarefeiro.model.Evento;
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
public interface EventoRepository extends JpaRepository<Evento, Long> {
    Optional<Evento> findByUuid(UUID uuid);
    Page<Evento> findByUtilizadorIdOrderByCriadoEmDesc(Long utilizadorId, Pageable pageable);
    List<Evento> findByUtilizadorIdAndProcessadoFalseOrderByCriadoEmAsc(Long utilizadorId);
    long countByUtilizadorIdAndProcessadoFalse(Long utilizadorId);
    long countByUtilizadorId(Long utilizadorId);

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.utilizador.id = :uid AND e.tipo = :tipo")
    long countByTipo(@Param("uid") Long utilizadorId, @Param("tipo") String tipo);
}
