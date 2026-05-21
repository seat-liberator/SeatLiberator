package com.seatliberator.seatliberator.board.persistence.board.row;

import com.seatliberator.seatliberator.board.application.post.port.in.result.BoardPostSummaryResult;

import java.time.Instant;
import java.util.UUID;

public record BoardPostSummaryRow(
        UUID boardId,
        UUID postId,
        UUID categoryId,
        String categoryName,
        String userId,
        String title,
        Instant createdAt,
        Instant updatedAt,
        Long commentCount
) {
    public BoardPostSummaryResult toResult() {
        return new BoardPostSummaryResult(
                boardId,
                postId,
                categoryId,
                categoryName,
                userId,
                title,
                createdAt,
                updatedAt,
                commentCount
        );
    }
}
