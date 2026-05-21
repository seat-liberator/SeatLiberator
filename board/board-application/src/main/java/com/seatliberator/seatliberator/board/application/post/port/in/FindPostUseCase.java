package com.seatliberator.seatliberator.board.application.post.port.in;

import com.seatliberator.seatliberator.board.application.post.port.in.query.FindPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;

public interface FindPostUseCase {
    PostResult find(FindPostQuery query);
}
