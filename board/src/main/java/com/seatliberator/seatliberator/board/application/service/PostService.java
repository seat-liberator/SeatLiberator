package com.seatliberator.seatliberator.board.application.service;

import com.seatliberator.seatliberator.board.application.exception.BoardNotFoundException;
import com.seatliberator.seatliberator.board.application.exception.CategoryNotFoundException;
import com.seatliberator.seatliberator.board.application.exception.PostNotFoundException;
import com.seatliberator.seatliberator.board.application.port.in.PostEntry;
import com.seatliberator.seatliberator.board.application.port.in.PostManager;
import com.seatliberator.seatliberator.board.application.port.in.command.PostCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.PostDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.PostUpdateCommand;
import com.seatliberator.seatliberator.board.application.port.out.BoardStore;
import com.seatliberator.seatliberator.board.domain.Board;
import com.seatliberator.seatliberator.board.domain.Category;
import com.seatliberator.seatliberator.board.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements PostManager {
    private final BoardStore boardStore;

    @Override
    @Transactional
    public PostEntry create(PostCreateCommand command) {
        var board = findBoardOrThrow(command.boardId());
        var category = findCategoryOrThrow(board, command.categoryId());

        var title = Optional.ofNullable(command.title())
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("Post title is required."));
        var content = Optional.ofNullable(command.content())
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("Post content is required."));

        var post = board.addPost(title, content, category);
        boardStore.save(board);
        return PostEntry.of(post);
    }

    @Override
    @Transactional
    public PostEntry update(PostUpdateCommand command) {
        var board = findBoardOrThrow(command.boardId());
        var post = findPostOrThrow(board, command.postId());

        var newTitle = Optional.ofNullable(command.title())
                .orElse(post.getTitle());
        var newContent = Optional.ofNullable(command.content())
                .orElse(post.getContent());
        var newCategory = Optional.ofNullable(command.categoryId())
                .map(categoryId -> findCategoryOrThrow(board, categoryId))
                .orElse(post.getCategory());

        post.setTitle(newTitle);
        post.setContent(newContent);
        board.changePostCategory(post, newCategory);
        boardStore.save(board);

        return PostEntry.of(post);
    }

    @Override
    @Transactional
    public void delete(PostDeleteCommand command) {
        var board = findBoardOrThrow(command.boardId());
        var post = findPostOrThrow(board, command.postId());
        board.removePost(post);
        boardStore.save(board);
    }

    @Override
    @Transactional(readOnly = true)
    public PostEntry get(UUID boardId, UUID postId) {
        var board = findBoardOrThrow(boardId);
        return PostEntry.of(findPostOrThrow(board, postId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostEntry> getAll(UUID boardId) {
        var board = findBoardOrThrow(boardId);
        return board.getPosts().stream()
                .map(PostEntry::of)
                .toList();
    }

    private Board findBoardOrThrow(UUID boardId) {
        return boardStore.getSingle(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }

    private Post findPostOrThrow(Board board, UUID postId) {
        return board.findPost(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    private Category findCategoryOrThrow(Board board, UUID categoryId) {
        return board.findCategory(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }
}
