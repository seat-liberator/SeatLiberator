package com.seatliberator.seatliberator.board.application.port.in;

import com.seatliberator.seatliberator.board.application.port.in.command.PostCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.PostDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.PostUpdateCommand;

import java.util.List;
import java.util.UUID;

public interface PostManager {
    PostEntry create(PostCreateCommand command);

    PostEntry update(PostUpdateCommand command);

    void delete(PostDeleteCommand command);

    PostEntry get(UUID boardId, UUID postId);

    List<PostEntry> getAll(UUID boardId);
}
