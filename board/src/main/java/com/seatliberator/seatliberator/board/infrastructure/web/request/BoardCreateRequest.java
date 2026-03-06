package com.seatliberator.seatliberator.board.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;

public record BoardCreateRequest(
        @NotBlank(message = "Board name is required.")
        String name,
        String description
) {
}
