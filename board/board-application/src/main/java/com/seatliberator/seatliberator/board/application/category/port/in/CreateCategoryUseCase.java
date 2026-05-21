package com.seatliberator.seatliberator.board.application.category.port.in;

import com.seatliberator.seatliberator.board.application.category.port.in.command.CreateCategoryCommand;
import com.seatliberator.seatliberator.board.application.category.port.in.result.CategoryResult;

public interface CreateCategoryUseCase {
    CategoryResult create(CreateCategoryCommand command);
}
