package com.seatliberator.seatliberator.board.application.post.port.out;

import com.seatliberator.seatliberator.board.domain.Post;

import java.util.Optional;
import java.util.UUID;

public interface PostReader {
    boolean existsById(UUID id);

    Optional<Post> findById(UUID id);
}
