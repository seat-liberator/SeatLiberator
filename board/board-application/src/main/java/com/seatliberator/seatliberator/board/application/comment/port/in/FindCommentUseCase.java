package com.seatliberator.seatliberator.board.application.comment.port.in;

import com.seatliberator.seatliberator.board.application.comment.port.in.query.FindCommentQuery;
import com.seatliberator.seatliberator.board.application.comment.port.in.result.CommentResult;

public interface FindCommentUseCase {
    CommentResult find(FindCommentQuery query);
}
