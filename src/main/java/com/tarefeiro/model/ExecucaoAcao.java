package com.tarefeiro.model;

import com.tarefeiro.enums.EstadoExecucao;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "execucoes_acao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecucaoAcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regra_id", nullable = false)
    private RegraAutomacao regra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilizador_id", nullable = false)
    private Utilizador utilizador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoExecucao estado = EstadoExecucao.PENDENTE;

    @Column(columnDefinition = "TEXT")
    private String resultado;

    @Column(name = "erro_detalhes", columnDefinition = "TEXT")
    private String erroDetalhes;

    @Column(name = "duracao_ms")
    private Integer duracaoMs;

    @Column(nullable = false)
    @Builder.Default
    private Integer tentativas = 0;

    @Column(name = "iniciado_em", nullable = false, updatable = false)
    private LocalDateTime iniciadoEm;

    @Column(name = "concluido_em")
    private LocalDateTime concluidoEm;

    /*
     * JSONB Metadata
     */
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    @PrePersist
    protected void onCreate() {

        iniciadoEm = LocalDateTime.now();

        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        if (metadata == null) {
            metadata = new HashMap<>();
        }
    }

    public void marcarSucesso(String resultado, long duracaoMs) {

        this.estado = EstadoExecucao.SUCESSO;
        this.resultado = resultado;
        this.duracaoMs = (int) duracaoMs;
        this.concluidoEm = LocalDateTime.now();

        metadata.put("estado", "SUCESSO");
        metadata.put("duracaoMs", duracaoMs);
        metadata.put("concluidoEm", this.concluidoEm.toString());
    }

    public void marcarFalha(String erro) {

        this.estado = EstadoExecucao.FALHA;
        this.erroDetalhes = erro;
        this.tentativas++;
        this.concluidoEm = LocalDateTime.now();

        metadata.put("estado", "FALHA");
        metadata.put("erro", erro);
        metadata.put("tentativas", tentativas);
        metadata.put("concluidoEm", this.concluidoEm.toString());
    }
}