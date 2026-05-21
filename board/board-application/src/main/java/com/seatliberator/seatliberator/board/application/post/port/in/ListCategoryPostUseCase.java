package com.seatliberator.seatliberator.board.application.post.port.in;

import com.seatliberator.seatliberator.board.application.post.port.in.query.ListCategoryPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;

import java.util.List;

public interface ListCategoryPostUseCase {
    List<PostResult> list(ListCategoryPostQuery query);
}
