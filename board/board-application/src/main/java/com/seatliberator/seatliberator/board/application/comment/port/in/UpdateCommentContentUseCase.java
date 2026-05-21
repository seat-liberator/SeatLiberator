package com.seatliberator.seatliberator.board.application.comment.port.in;

import com.seatliberator.seatliberator.board.application.comment.port.in.command.UpdateCommentContentCommand;
import com.seatliberator.seatliberator.board.application.comment.port.in.result.CommentResult;

public interface UpdateCommentContentUseCase {
    CommentResult update(UpdateCommentContentCommand command);
}
