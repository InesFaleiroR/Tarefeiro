package com.tarefeiro.repository;

import com.tarefeiro.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUtilizadorIdOrderByCriadoEmDesc(Long utilizadorId);
    List<Notificacao> findByUtilizadorIdAndLidaFalseOrderByCriadoEmDesc(Long utilizadorId);
    long countByUtilizadorIdAndLidaFalse(Long utilizadorId);
    void deleteByUtilizadorIdAndLidaTrue(Long utilizadorId);
}
