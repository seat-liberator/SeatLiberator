package com.seatliberator.seatliberator.board.application.post.port.in;

import com.seatliberator.seatliberator.board.application.post.port.in.command.UpdatePostContentCommand;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;

public interface UpdatePostContentUseCase {
    PostResult update(UpdatePostContentCommand command);
}
