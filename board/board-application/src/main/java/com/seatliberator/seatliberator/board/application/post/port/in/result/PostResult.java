package com.seatliberator.seatliberator.board.application.post.port.in.result;

import com.seatliberator.seatliberator.board.domain.Post;

import java.time.Instant;
import java.util.UUID;

public record PostResult(
        UUID postId,
        UUID boardId,
        UUID categoryId,
        String userId,
        String title,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
    public static PostResult from(Post post) {
        return new PostResult(
                post.getId(),
                post.getBoardId(),
                post.getCategoryId(),
                post.getUserId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
