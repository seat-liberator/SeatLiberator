package com.seatliberator.seatliberator.board.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PostCreateRequest(
        @NotNull(message = "Category id is required.")
        UUID categoryId,
        @NotBlank(message = "Post title is required.")
        String title,
        @NotBlank(message = "Post content is required.")
        String content
) {
}
