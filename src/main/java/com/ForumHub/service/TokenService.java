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

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String gerarToken(Usuario usuario) {
        try {            
            logger.info("Generating JWT token for user: {}", usuario.getLogin());
            String token = Jwts.builder()
                    .setSubject(usuario.getLogin())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
            logger.info("JWT token generated successfully for user: {}", usuario.getLogin());
                return token;
        } catch (JwtException e) {
            logger.error("Failed to generate JWT token for user: {}. Error: {}", usuario.getLogin(), e.getMessage(), e);
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public String getSubject(String token) {
        if (token == null || token.isBlank()) {
            logger.warn("Token is null or empty");
            throw new IllegalArgumentException("Token is null or empty");
        }
        try {
            logger.info("Validating JWT token");
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            String subject = claims.getSubject();
            logger.info("JWT token validated successfully. Subject: {}", subject);
            return subject;
        } catch (JwtException e) {
            logger.error("Failed to validate JWT token: {}. Error: {}", token, e.getMessage(), e);
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage(), e);
        }
    }
}