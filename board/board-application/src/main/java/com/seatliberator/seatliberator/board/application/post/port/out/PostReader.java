package com.seatliberator.seatliberator.board.application.post.port.out;

import com.seatliberator.seatliberator.board.application.post.port.out.filter.PostFilter;
import com.seatliberator.seatliberator.board.domain.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostReader {
    boolean existsById(UUID id);

    Optional<Post> findById(UUID id);

    List<Post> findByFilter(PostFilter filter);
}
