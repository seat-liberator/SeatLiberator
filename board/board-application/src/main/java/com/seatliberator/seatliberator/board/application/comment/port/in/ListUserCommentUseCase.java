package com.seatliberator.seatliberator.board.application.comment.port.in;

import com.seatliberator.seatliberator.board.application.comment.port.in.query.ListUserCommentQuery;
import com.seatliberator.seatliberator.board.application.comment.port.in.result.CommentResult;

import java.util.List;

public interface ListUserCommentUseCase {
    List<CommentResult> list(ListUserCommentQuery query);
}
