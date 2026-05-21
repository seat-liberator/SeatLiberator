package com.seatliberator.seatliberator.board.application.category.port.in;

import com.seatliberator.seatliberator.board.application.category.port.in.query.FindCategoryQuery;
import com.seatliberator.seatliberator.board.application.category.port.in.result.CategoryResult;

public interface FindCategoryUseCase {
    CategoryResult find(FindCategoryQuery query);
}
