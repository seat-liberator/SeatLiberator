package com.seatliberator.seatliberator.board.application.port.in;

import com.seatliberator.seatliberator.board.application.port.in.command.CategoryCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryUpdateCommand;

import java.util.List;
import java.util.UUID;

public interface CategoryManager {
    CategoryEntry create(CategoryCreateCommand command);

    CategoryEntry update(CategoryUpdateCommand command);

    void delete(CategoryDeleteCommand command);

    CategoryEntry get(UUID boardId, UUID categoryId);

    List<CategoryEntry> getAll(UUID boardId);
}
