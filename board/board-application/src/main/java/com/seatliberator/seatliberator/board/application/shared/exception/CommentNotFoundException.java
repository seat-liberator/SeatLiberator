package com.seatliberator.seatliberator.board.application.shared.exception;

import java.util.UUID;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(UUID commentId) {
        super("Comment not found: " + commentId);
    }
}
