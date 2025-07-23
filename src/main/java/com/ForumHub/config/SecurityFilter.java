package com.ForumHub.config;

import com.ForumHub.service.TokenService;
import com.ForumHub.model.Usuario;
import com.ForumHub.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final Key secretKey = Keys.hmacShaKeyFor("chavesecretasupersegura1234567890abcdef".getBytes());

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @NonNull
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/auth");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {
        String token = getToken(request);
        if (token != null && !token.isBlank()) {
            try {
                String username = tokenService.getSubject(token);
                if (username != null) {
                    Usuario usuario = usuarioRepository.findByLogin(username)
                            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Usuário Autenticado: " + username);
                }
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                logger.error("Token JWT inválido: " + token, e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                logger.error("Processando erro JWT: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            logger.warn("Não foi encontrado um token JWT válido");
        }
        chain.doFilter(request, response);
    }
    /*@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null) {
            String login = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            var usuario = usuarioRepository.findByLogin(login);
            if (usuario.isPresent()) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        usuario.get(), null, usuario.get().getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }*/


    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Auutorização");
        logger.info("Header de Autorização: " + header);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.replace("Bearer ", "").trim();
            logger.info("Token Extraído: " + token);
            return token;
        }
        return null;
    }
    /*private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }
        return null;
    }*/
}