package com.tarefeiro.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "eventos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilizador_id", nullable = false)
    private Utilizador utilizador;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 300)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 100)
    private String fonte;

    @Column(columnDefinition = "jsonb")
    private String payload;

    @Column(nullable = false)
    @Builder.Default
    private Boolean processado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regra_id")
    private RegraAutomacao regra;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        if (uuid == null) uuid = UUID.randomUUID();
    }
}
