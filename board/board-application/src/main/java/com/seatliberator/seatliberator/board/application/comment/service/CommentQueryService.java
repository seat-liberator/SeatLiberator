package com.seatliberator.seatliberator.board.application.comment.service;

import com.seatliberator.seatliberator.board.application.comment.port.in.FindCommentUseCase;
import com.seatliberator.seatliberator.board.application.comment.port.in.ListPostCommentUseCase;
import com.seatliberator.seatliberator.board.application.comment.port.in.ListUserCommentUseCase;
import com.seatliberator.seatliberator.board.application.comment.port.in.query.FindCommentQuery;
import com.seatliberator.seatliberator.board.application.comment.port.in.query.ListPostCommentQuery;
import com.seatliberator.seatliberator.board.application.comment.port.in.query.ListUserCommentQuery;
import com.seatliberator.seatliberator.board.application.comment.port.in.result.CommentResult;
import com.seatliberator.seatliberator.board.application.comment.port.out.CommentReader;
import com.seatliberator.seatliberator.board.application.post.port.out.PostReader;
import com.seatliberator.seatliberator.board.application.shared.exception.CommentNotFoundException;
import com.seatliberator.seatliberator.board.application.shared.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService implements
        FindCommentUseCase,
        ListPostCommentUseCase,
        ListUserCommentUseCase {

    private final CommentReader reader;
    private final PostReader postReader;

    @Override
    public CommentResult find(FindCommentQuery query) {
        var commentId = query.commentId();
        return reader.findById(commentId)
                .map(CommentResult::from)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    @Override
    public List<CommentResult> list(ListPostCommentQuery query) {
        var postId = query.postId();
        var existsPost = postReader.existsById(postId);
        if (!existsPost) throw new PostNotFoundException(postId);

        return reader.findByFilter(query.toFilter()).stream()
                .map(CommentResult::from)
                .toList();
    }

    @Override
    public List<CommentResult> list(ListUserCommentQuery query) {
        return reader.findByFilter(query.toFilter()).stream()
                .map(CommentResult::from)
                .toList();
    }
}
