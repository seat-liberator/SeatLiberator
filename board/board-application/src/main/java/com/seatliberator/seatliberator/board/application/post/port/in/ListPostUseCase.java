package com.seatliberator.seatliberator.board.application.post.port.in;

import com.seatliberator.seatliberator.board.application.post.port.in.query.ListPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;

import java.util.List;

public interface ListPostUseCase {
    List<PostResult> list(ListPostQuery query);
}
