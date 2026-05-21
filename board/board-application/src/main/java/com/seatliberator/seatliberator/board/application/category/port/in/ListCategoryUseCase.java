package com.seatliberator.seatliberator.board.application.category.port.in;

import com.seatliberator.seatliberator.board.application.category.port.in.query.ListCategoryQuery;
import com.seatliberator.seatliberator.board.application.category.port.in.result.CategoryResult;

import java.util.List;

public interface ListCategoryUseCase {
    List<CategoryResult> list(ListCategoryQuery query);
}
