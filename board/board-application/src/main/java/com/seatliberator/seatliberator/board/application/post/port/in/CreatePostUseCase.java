package com.seatliberator.seatliberator.board.application.post.port.in;

import com.seatliberator.seatliberator.board.application.post.port.in.command.CreatePostCommand;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;

public interface CreatePostUseCase {
    PostResult create(CreatePostCommand command);
}
