package com.seatliberator.seatliberator.board.application.comment.port.in.result;

import com.seatliberator.seatliberator.board.domain.Comment;

import java.time.Instant;
import java.util.UUID;

public record CommentResult(
        UUID commentId,
        UUID postId,
        String userId,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
    public static CommentResult from(Comment comment) {
        return new CommentResult(
                comment.getId(),
                comment.getPostId(),
                comment.getUserId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
