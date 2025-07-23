package com.ForumHub.service;

import java.util.Date;

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
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String gerarToken(Usuario usuario) {
        try {
            logger.info("Gerando token JWT para usuário: {}", usuario.getLogin());
            String token = Jwts.builder()
                    .setSubject(usuario.getLogin())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                    .compact();
            logger.info("Token JWT gerado com sucesso para usuário: {}", usuario.getLogin());
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
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String subject = claims.getSubject();
            logger.info("Token JWT validado com sucesso. Subject: {}", subject);
            return subject;
        } catch (JwtException e) {
            logger.error("Falha ao validar token JWT: {}. Erro: {}", token, e.getMessage(), e);
            throw new IllegalArgumentException("Token JWT inválido: " + e.getMessage(), e);
        }
    }
}