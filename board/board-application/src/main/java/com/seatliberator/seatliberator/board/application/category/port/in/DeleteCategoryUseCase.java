package com.seatliberator.seatliberator.board.application.category.port.in;

import com.seatliberator.seatliberator.board.application.category.port.in.command.DeleteCategoryCommand;

public interface DeleteCategoryUseCase {
    void delete(DeleteCategoryCommand command);
}
