package com.seatliberator.seatliberator.board.application.category.service;

import com.seatliberator.seatliberator.board.application.board.port.out.BoardReader;
import com.seatliberator.seatliberator.board.application.category.port.in.CreateCategoryUseCase;
import com.seatliberator.seatliberator.board.application.category.port.in.DeleteCategoryUseCase;
import com.seatliberator.seatliberator.board.application.category.port.in.UpdateCategoryDescriptionUseCase;
import com.seatliberator.seatliberator.board.application.category.port.in.UpdateCategoryNameUseCase;
import com.seatliberator.seatliberator.board.application.category.port.in.command.CreateCategoryCommand;
import com.seatliberator.seatliberator.board.application.category.port.in.command.DeleteCategoryCommand;
import com.seatliberator.seatliberator.board.application.category.port.in.command.UpdateCategoryDescriptionCommand;
import com.seatliberator.seatliberator.board.application.category.port.in.command.UpdateCategoryNameCommand;
import com.seatliberator.seatliberator.board.application.category.port.in.result.CategoryResult;
import com.seatliberator.seatliberator.board.application.category.port.out.CategoryReader;
import com.seatliberator.seatliberator.board.application.category.port.out.CategoryStore;
import com.seatliberator.seatliberator.board.application.shared.exception.BoardNotFoundException;
import com.seatliberator.seatliberator.board.application.shared.exception.CategoryNotFoundException;
import com.seatliberator.seatliberator.board.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryCommandService implements
        CreateCategoryUseCase,
        UpdateCategoryNameUseCase,
        UpdateCategoryDescriptionUseCase,
        DeleteCategoryUseCase {

    private final CategoryReader reader;
    private final CategoryStore store;

    private final BoardReader boardReader;
    private final Clock clock;

    @Override
    public CategoryResult create(CreateCategoryCommand command) {
        var boardId = command.boardId();
        var exists = boardReader.existsById(boardId);
        if (!exists) throw new BoardNotFoundException(boardId);

        var now = clock.instant();
        var category = Category.of(
                command.boardId(),
                command.name(),
                command.description(),
                now
        );

        var saved = store.save(category);

        return CategoryResult.from(saved);
    }

    @Override
    public CategoryResult update(UpdateCategoryNameCommand command) {
        var categoryId = command.categoryId();
        var category = reader.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        category.updateName(category.getName());

        var saved = store.save(category);

        return CategoryResult.from(category);
    }

    @Override
    public CategoryResult update(UpdateCategoryDescriptionCommand command) {
        var categoryId = command.categoryId();
        var category = reader.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        category.updateDescription(command.description());

        var saved = store.save(category);

        return CategoryResult.from(saved);
    }

    @Override
    public void delete(DeleteCategoryCommand command) {
        var categoryId = command.categoryId();
        var category = reader.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        store.delete(category);
    }
}
