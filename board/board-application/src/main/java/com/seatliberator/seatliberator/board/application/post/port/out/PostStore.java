package com.seatliberator.seatliberator.board.application.post.port.out;

import com.seatliberator.seatliberator.board.domain.Post;

public interface PostStore {
    Post save(Post post);

    void delete(Post post);
}
