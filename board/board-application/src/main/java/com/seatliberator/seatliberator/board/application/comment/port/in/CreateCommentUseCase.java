package com.seatliberator.seatliberator.board.application.comment.port.in;

import com.seatliberator.seatliberator.board.application.comment.port.in.command.CreateCommentCommand;
import com.seatliberator.seatliberator.board.application.comment.port.in.result.CommentResult;

public interface CreateCommentUseCase {
    CommentResult create(CreateCommentCommand command);
}
