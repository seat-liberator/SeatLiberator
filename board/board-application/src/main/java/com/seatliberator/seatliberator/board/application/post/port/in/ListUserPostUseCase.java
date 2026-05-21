package com.seatliberator.seatliberator.board.application.post.port.in;

import com.seatliberator.seatliberator.board.application.post.port.in.query.ListUserPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;

import java.util.List;

public interface ListUserPostUseCase {
    List<PostResult> list(ListUserPostQuery query);
}
