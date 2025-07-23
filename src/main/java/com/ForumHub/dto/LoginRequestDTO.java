package com.ForumHub.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank @Login String login,
        @NotBlank String senha) {
}