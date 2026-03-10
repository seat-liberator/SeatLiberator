package com.seatliberator.seatliberator.board.application.port.in;

import com.seatliberator.seatliberator.board.domain.Post;

import java.util.UUID;

public record PostEntry(
        UUID postId,
        String title,
        String content
) {
    public static PostEntry of(Post post) {
        return new PostEntry(post.getId(), post.getTitle(), post.getContent());
    }
}
