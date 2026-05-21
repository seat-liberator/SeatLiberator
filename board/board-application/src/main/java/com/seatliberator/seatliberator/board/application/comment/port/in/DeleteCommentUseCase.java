package com.seatliberator.seatliberator.board.application.comment.port.in;

import com.seatliberator.seatliberator.board.application.comment.port.in.command.DeleteCommentCommand;

public interface DeleteCommentUseCase {
    void delete(DeleteCommentCommand command);
}
