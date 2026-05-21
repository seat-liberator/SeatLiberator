package com.seatliberator.seatliberator.board.application.comment.port.out;

import com.seatliberator.seatliberator.board.domain.Comment;

public interface CommentStore {
    Comment save(Comment comment);

    void delete(Comment comment);
}
