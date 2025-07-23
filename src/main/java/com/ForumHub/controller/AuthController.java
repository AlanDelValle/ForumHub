package com.ForumHub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ForumHub.dto.LoginRequestDTO;
import com.ForumHub.model.Usuario;
import com.ForumHub.security.DadosTokenJWT;
import com.ForumHub.service.TokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;
  
    @PostMapping("/login")
    public ResponseEntity<DadosTokenJWT> login(@RequestBody @Valid LoginRequestDTO dados) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        Authentication authentication = manager.authenticate(authToken);

        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
    

    /*@PostMapping
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDTO dados) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        Authentication authentication = authenticationManager.authenticate(authToken);
        String token = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        return ResponseEntity.ok(token);
    }*/
}