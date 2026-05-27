package com.tarefeiro.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "regras_automacao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegraAutomacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilizador_id", nullable = false)
    private Utilizador utilizador;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "condicao_tipo", nullable = false, length = 50)
    private String condicaoTipo;

    @Column(name = "condicao_valor", nullable = false, columnDefinition = "TEXT")
    private String condicaoValor;

    @Column(name = "acao_tipo", nullable = false, length = 50)
    private String acaoTipo;

    @Column(name = "acao_config", columnDefinition = "jsonb")
    private String acaoConfig;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativa = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer prioridade = 0;

    @Column(name = "execucoes_total", nullable = false)
    @Builder.Default
    private Integer execucoesTotal = 0;

    @Column(name = "ultima_execucao")
    private LocalDateTime ultimaExecucao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "regra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ExecucaoAcao> execucoes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
        if (uuid == null) uuid = UUID.randomUUID();
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public void incrementarExecucoes() {
        this.execucoesTotal++;
        this.ultimaExecucao = LocalDateTime.now();
    }
}
