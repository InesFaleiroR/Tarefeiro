package com.tarefeiro.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {

    @Value("${tarefeiro.jwt.secret:dGFyZWZlaXJvLXNlY3JldC1rZXktMjAyNS1zdXBlci1zZWN1cmUtcGxlYXNlLWNoYW5nZS1pbi1wcm9kdWN0aW9u}")
    private String secret;

    @Value("${tarefeiro.jwt.expiration-ms:86400000}")
    private long expirationMs;

    @Value("${tarefeiro.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String gerarToken(UserDetails userDetails) {
        return gerarToken(new HashMap<>(), userDetails);
    }

    public String gerarToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSigningKey())
            .compact();
    }

    public String gerarRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
            .claim("type", "refresh")
            .signWith(getSigningKey())
            .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public boolean isTokenValido(String token, UserDetails userDetails) {
        try {
            String email = extrairEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpirado(token);
        } catch (JwtException e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
