package com.tarefeiro.config;

import com.tarefeiro.security.JwtAuthFilter;
import com.tarefeiro.service.impl.UtilizadorDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UtilizadorDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final DataSource dataSource;

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Cadeia para /api/** — stateless JWT, sem sessão, sem CSRF
    // ─────────────────────────────────────────────────────────────────────────
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/v1/webhooks/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/webhooks/externo").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/webhooks/gmail").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/webhooks/link").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setContentType("application/json;charset=UTF-8");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write(
                        "{\"sucesso\":false,\"mensagem\":\"Nao autenticado. Inclui: Authorization: Bearer <TOKEN>\"}"
                    );
                })
                .accessDeniedHandler((req, res, e) -> {
                    res.setContentType("application/json;charset=UTF-8");
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.getWriter().write(
                        "{\"sucesso\":false,\"mensagem\":\"Acesso negado.\"}"
                    );
                })
            );
        return http.build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Cadeia para /** — session-based Web (Thymeleaf + formulários)
    // ─────────────────────────────────────────────────────────────────────────
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", "/utilizadores/registo",
                    "/css/**", "/js/**", "/images/**", "/fonts/**",
                    "/actuator/health"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("senha")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?erro")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?saiu")
                .deleteCookies("JSESSIONID", "remember-me")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .rememberMe(rm -> rm
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(30 * 24 * 60 * 60)
                .userDetailsService(userDetailsService)
                .key("tarefeiro-remember-me-key")
            );
        return http.build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Beans partilhados
    // ─────────────────────────────────────────────────────────────────────────

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
