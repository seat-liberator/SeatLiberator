package com.seatliberator.seatliberator.board.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;

public record PostCreateRequest(
        @NotBlank(message = "Post title is required.")
        String title,
        @NotBlank(message = "Post content is required.")
        String content
) {
}
