package com.ForumHub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TopicoRequestDTO(
        @NotBlank String titulo,
        @NotBlank String mensagem,
        @NotBlank String autor,
        @NotBlank String curso, 
        @NotNull int year) {
}