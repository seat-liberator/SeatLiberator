package com.seatliberator.seatliberator.board.application.category.port.in;

import com.seatliberator.seatliberator.board.application.category.port.in.command.UpdateCategoryDescriptionCommand;
import com.seatliberator.seatliberator.board.application.category.port.in.result.CategoryResult;

public interface UpdateCategoryDescriptionUseCase {
    CategoryResult update(UpdateCategoryDescriptionCommand command);
}
