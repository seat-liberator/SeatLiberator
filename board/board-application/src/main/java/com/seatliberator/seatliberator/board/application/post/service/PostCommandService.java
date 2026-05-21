package com.seatliberator.seatliberator.board.application.post.service;

import com.seatliberator.seatliberator.board.application.board.port.out.BoardReader;
import com.seatliberator.seatliberator.board.application.category.port.out.CategoryReader;
import com.seatliberator.seatliberator.board.application.post.port.in.*;
import com.seatliberator.seatliberator.board.application.post.port.in.command.*;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;
import com.seatliberator.seatliberator.board.application.post.port.out.PostReader;
import com.seatliberator.seatliberator.board.application.post.port.out.PostStore;
import com.seatliberator.seatliberator.board.application.shared.exception.BoardNotFoundException;
import com.seatliberator.seatliberator.board.application.shared.exception.CategoryNotFoundException;
import com.seatliberator.seatliberator.board.application.shared.exception.PostNotFoundException;
import com.seatliberator.seatliberator.board.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommandService implements
        CreatePostUseCase,
        UpdatePostTitleUseCase,
        UpdatePostContentUseCase,
        UpdatePostCategoryUseCase,
        DeletePostUseCase {

    private final PostReader reader;
    private final PostStore store;

    private final BoardReader boardReader;
    private final CategoryReader categoryReader;
    private final Clock clock;

    @Override
    public PostResult create(CreatePostCommand command) {
        var boardId = command.boardId();
        var categoryId = command.categoryId();

        var existsBoard = boardReader.existsById(boardId);
        if (!existsBoard) throw new BoardNotFoundException(boardId);

        var existsCategory = categoryReader.existsById(categoryId);
        if (!existsCategory) throw new CategoryNotFoundException(categoryId);

        var now = clock.instant();
        var post = Post.of(
                boardId,
                categoryId,
                command.userId(),
                command.title(),
                command.content(),
                now
        );

        var saved = store.save(post);

        return PostResult.from(saved);
    }

    @Override
    public void delete(DeletePostCommand command) {
        var postId = command.postId();
        var post = reader.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        store.delete(post);
    }

    @Override
    public PostResult update(UpdatePostCategoryCommand command) {
        var categoryId = command.categoryId();
        var existsCategory = categoryReader.existsById(categoryId);
        if (!existsCategory) throw new CategoryNotFoundException(categoryId);

        var postId = command.postId();
        var post = reader.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        var now = clock.instant();
        post.updateCategoryId(categoryId, now);

        var saved = store.save(post);

        return PostResult.from(saved);
    }

    @Override
    public PostResult update(UpdatePostTitleCommand command) {
        var postId = command.postId();
        var post = reader.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        var now = clock.instant();
        post.updateTitle(command.title(), now);

        var saved = store.save(post);

        return PostResult.from(saved);
    }

    @Override
    public PostResult update(UpdatePostContentCommand command) {
        var postId = command.postId();
        var post = reader.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        var now = clock.instant();
        post.updateContent(command.content(), now);

        var saved = store.save(post);

        return PostResult.from(saved);
    }
}
