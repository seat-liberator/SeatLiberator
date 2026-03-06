package com.seatliberator.seatliberator.board.infrastructure.web.request;

public record PostCreateRequest(
        String title,
        String content
) {
}
