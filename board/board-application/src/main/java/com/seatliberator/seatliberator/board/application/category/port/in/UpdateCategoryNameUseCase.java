package com.seatliberator.seatliberator.board.application.category.port.in;

import com.seatliberator.seatliberator.board.application.category.port.in.command.UpdateCategoryNameCommand;
import com.seatliberator.seatliberator.board.application.category.port.in.result.CategoryResult;

public interface UpdateCategoryNameUseCase {
    CategoryResult update(UpdateCategoryNameCommand command);
}
