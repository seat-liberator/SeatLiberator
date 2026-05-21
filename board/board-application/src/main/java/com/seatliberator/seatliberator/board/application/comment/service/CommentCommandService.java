package com.seatliberator.seatliberator.board.application.comment.service;

import com.seatliberator.seatliberator.board.application.comment.port.in.CreateCommentUseCase;
import com.seatliberator.seatliberator.board.application.comment.port.in.DeleteCommentUseCase;
import com.seatliberator.seatliberator.board.application.comment.port.in.UpdateCommentContentUseCase;
import com.seatliberator.seatliberator.board.application.comment.port.in.command.CreateCommentCommand;
import com.seatliberator.seatliberator.board.application.comment.port.in.command.DeleteCommentCommand;
import com.seatliberator.seatliberator.board.application.comment.port.in.command.UpdateCommentContentCommand;
import com.seatliberator.seatliberator.board.application.comment.port.in.result.CommentResult;
import com.seatliberator.seatliberator.board.application.comment.port.out.CommentReader;
import com.seatliberator.seatliberator.board.application.comment.port.out.CommentStore;
import com.seatliberator.seatliberator.board.application.post.port.out.PostReader;
import com.seatliberator.seatliberator.board.application.shared.exception.CommentNotFoundException;
import com.seatliberator.seatliberator.board.application.shared.exception.PostNotFoundException;
import com.seatliberator.seatliberator.board.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandService implements
        CreateCommentUseCase,
        UpdateCommentContentUseCase,
        DeleteCommentUseCase {

    private final CommentReader reader;
    private final CommentStore store;

    private final PostReader postReader;
    private final Clock clock;

    @Override
    public CommentResult create(CreateCommentCommand command) {
        var postId = command.postId();
        var existsPost = postReader.existsById(postId);
        if (!existsPost) throw new PostNotFoundException(postId);

        var now = clock.instant();
        var comment = Comment.of(postId, command.userId(), command.content(), now);

        var saved = store.save(comment);

        return CommentResult.from(saved);
    }

    @Override
    public CommentResult update(UpdateCommentContentCommand command) {
        var commentId = command.commentId();
        var comment = reader.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        var now = clock.instant();
        comment.updateContent(command.content(), now);

        var saved = store.save(comment);

        return CommentResult.from(saved);
    }

    @Override
    public void delete(DeleteCommentCommand command) {
        var commentId = command.commentId();
        var comment = reader.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        store.delete(comment);
    }
}
