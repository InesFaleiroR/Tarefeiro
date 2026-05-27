package com.tarefeiro.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilizador_id", nullable = false)
    private Utilizador utilizador;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String tipo = "INFO";

    @Column(nullable = false)
    @Builder.Default
    private Boolean lida = false;

    private String link;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
    }
}
