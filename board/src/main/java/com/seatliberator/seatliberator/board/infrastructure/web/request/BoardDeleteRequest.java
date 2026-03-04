package com.seatliberator.seatliberator.board.infrastructure.web.request;

import java.util.UUID;

public record BoardDeleteRequest(
        UUID boardId
) {
}
