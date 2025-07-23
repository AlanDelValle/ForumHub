package com.ForumHub.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ForumHub.dto.LoginRequestDTO;
import com.ForumHub.model.Usuario;
import com.ForumHub.security.DadosTokenJWT;
import com.ForumHub.service.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO dados) {
        logger.info("Recebida requisição de login para usuário: {}, senha: {}", dados.login(), dados.senha());
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
            logger.info("Tentando autenticar usuário: {}", dados.login());
            Authentication authentication = manager.authenticate(authToken);
            logger.info("Autenticação bem-sucedida para usuário: {}", dados.login());
            String tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
            logger.info("Token gerado com sucesso para usuário: {}", dados.login());
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
        } catch (AuthenticationException e) {
            logger.error("Falha na autenticação para usuário: {}. Erro: {}", dados.login(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }
}