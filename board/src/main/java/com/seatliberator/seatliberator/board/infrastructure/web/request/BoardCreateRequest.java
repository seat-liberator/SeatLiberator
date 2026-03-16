package com.seatliberator.seatliberator.board.infrastructure.web.request;

public record BoardCreateRequest(
        String name,
        String description
) {
}
