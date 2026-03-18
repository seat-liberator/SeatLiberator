package com.seatliberator.seatliberator.board.application.service;

import com.seatliberator.seatliberator.board.application.exception.BoardNotFoundException;
import com.seatliberator.seatliberator.board.application.exception.CategoryNotFoundException;
import com.seatliberator.seatliberator.board.application.port.in.CategoryEntry;
import com.seatliberator.seatliberator.board.application.port.in.CategoryManager;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryUpdateCommand;
import com.seatliberator.seatliberator.board.application.port.out.BoardStore;
import com.seatliberator.seatliberator.board.domain.Board;
import com.seatliberator.seatliberator.board.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryManager {
    private final BoardStore boardStore;

    @Override
    @Transactional
    public CategoryEntry create(CategoryCreateCommand command) {
        var board = findBoardOrThrow(command.boardId());
        var name = Optional.ofNullable(command.name())
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Category name is required."));

        var category = board.addCategory(name, command.description());
        boardStore.save(board);
        return CategoryEntry.of(category);
    }

    @Override
    @Transactional
    public CategoryEntry update(CategoryUpdateCommand command) {
        var board = findBoardOrThrow(command.boardId());
        var category = findCategoryOrThrow(board, command.categoryId());

        var newName = Optional.ofNullable(command.name())
                .map(String::trim)
                .map(value -> {
                    if (value.isEmpty()) {
                        throw new IllegalArgumentException("Category name cannot be blank.");
                    }
                    return value;
                })
                .orElse(category.getName());
        var newDescription = Optional.ofNullable(command.description()).orElse(category.getDescription());

        category.setName(newName);
        category.setDescription(newDescription);
        boardStore.save(board);
        return CategoryEntry.of(category);
    }

    @Override
    @Transactional
    public void delete(CategoryDeleteCommand command) {
        var board = findBoardOrThrow(command.boardId());
        var category = findCategoryOrThrow(board, command.categoryId());
        if (!category.getPosts().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category with posts.");
        }
        board.removeCategory(category);
        boardStore.save(board);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryEntry get(UUID boardId, UUID categoryId) {
        var board = findBoardOrThrow(boardId);
        return CategoryEntry.of(findCategoryOrThrow(board, categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryEntry> getAll(UUID boardId) {
        var board = findBoardWithCategoriesOrThrow(boardId);
        return board.getCategories().stream()
                .map(CategoryEntry::of)
                .toList();
    }

    private Board findBoardOrThrow(UUID boardId) {
        return boardStore.getSingle(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }

    private Board findBoardWithCategoriesOrThrow(UUID boardId) {
        return boardStore.getSingleWithCategories(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }

    private Category findCategoryOrThrow(Board board, UUID categoryId) {
        return board.findCategory(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }
}
