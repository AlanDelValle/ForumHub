package com.ForumHub.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ForumHub.model.Usuario;
import com.ForumHub.repository.UsuarioRepository;
import com.ForumHub.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        logger.info("Verificando se deve filtrar o caminho: {}", path);
        return path.equals("/auth/login") || path.startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {
        String token = getToken(request);
        if (token != null && !token.isBlank()) {
            try {
                logger.info("Validando token JWT: {}", token);
                String username = tokenService.getSubject(token);
                if (username != null) {
                    Usuario usuario = usuarioRepository.findByLogin(username)
                            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Usuário autenticado com sucesso: {}", username);
                }
            } catch (IllegalArgumentException e) {
                logger.error("Token JWT inválido: {}. Erro: {}", token, e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                logger.error("Erro ao processar token JWT: {}. Erro: {}", token, e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            logger.warn("Não foi encontrado um token JWT válido para a requisição: {}. Header recebido: {}", request.getRequestURI(), request.getHeader("Authorization"));
        }
        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    logger.info("Header de Authorization bruto: " + header);        
        return header;    
    }

}