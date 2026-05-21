package com.seatliberator.seatliberator.board.application.post.port.in;

import com.seatliberator.seatliberator.board.application.post.port.in.command.DeletePostCommand;

public interface DeletePostUseCase {
    void delete(DeletePostCommand command);
}
