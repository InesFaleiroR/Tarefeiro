package com.tarefeiro.model;

import com.tarefeiro.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "utilizadores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Utilizador implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "utilizador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RegraAutomacao> regras = new ArrayList<>();

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override public String getPassword() { return senha; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return ativo; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return ativo; }
}
