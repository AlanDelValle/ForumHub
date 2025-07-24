package com.ForumHub.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "topicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @NotBlank
    @Column(nullable = false)
    private String status;

    @NotBlank
    @Column(nullable = false)
    private String autor;

    @NotBlank
    @Column(nullable = false)
    private String curso;

    @NotNull
    @Column(nullable = false)
    private int year;
}