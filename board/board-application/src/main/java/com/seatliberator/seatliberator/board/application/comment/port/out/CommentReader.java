package com.seatliberator.seatliberator.board.application.comment.port.out;

import com.seatliberator.seatliberator.board.application.comment.port.out.filter.CommentFilter;
import com.seatliberator.seatliberator.board.domain.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentReader {
    boolean existsById(UUID id);

    Optional<Comment> findById(UUID id);

    List<Comment> findByFilter(CommentFilter filter);
}
