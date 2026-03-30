package com.seatliberator.seatliberator.board.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank(message = "Category name is required.")
        String name,
        String description
) {
}
