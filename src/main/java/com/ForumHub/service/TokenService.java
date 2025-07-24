package com.ForumHub.service;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ForumHub.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Value("${jwt.secret}")
    private String secretBase64;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretBase64));
    }

    public String gerarToken(Usuario usuario) {
        try {
            logger.info("Gerando token JWT para usuário: {}", usuario.getLogin());
            String token = Jwts.builder()
                    .setSubject(usuario.getLogin())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
            logger.info("Token JWT gerado com sucesso para usuário: {}.", usuario.getLogin());
            return token;
        } catch (JwtException e) {
            logger.error("Falha ao gerar token JWT para usuário: {}. Erro: {}", usuario.getLogin(), e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    public String getSubject(String token) {
        if (token == null || token.isBlank()) {
            logger.warn("Token é nulo ou vazio");
            throw new IllegalArgumentException("Token é nulo ou vazio");
        }
        try {
            logger.info("Validando token JWT");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token.replace("Bearer ", "").trim())
                    .getBody();
            String subject = claims.getSubject();
            logger.info("Token JWT validado com sucesso. Subject: {}", subject);
            return subject;
        } catch (JwtException e) {
            logger.error("Falha ao validar token JWT. Erro: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Token JWT inválido: " + e.getMessage(), e);
        }
    }
}