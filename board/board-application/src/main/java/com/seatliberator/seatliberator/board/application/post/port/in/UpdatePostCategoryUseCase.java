package com.seatliberator.seatliberator.board.application.post.port.in;

import com.seatliberator.seatliberator.board.application.post.port.in.command.UpdatePostCategoryCommand;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;

public interface UpdatePostCategoryUseCase {
    PostResult update(UpdatePostCategoryCommand command);
}
