package com.seatliberator.seatliberator.board.application.comment.port.in;

import com.seatliberator.seatliberator.board.application.comment.port.in.query.ListPostCommentQuery;
import com.seatliberator.seatliberator.board.application.comment.port.in.result.CommentResult;

import java.util.List;

public interface ListPostCommentUseCase {
    List<CommentResult> list(ListPostCommentQuery query);
}
