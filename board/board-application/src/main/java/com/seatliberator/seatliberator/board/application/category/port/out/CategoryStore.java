package com.seatliberator.seatliberator.board.application.category.port.out;

import com.seatliberator.seatliberator.board.domain.Category;

public interface CategoryStore {
    Category save(Category category);

    void delete(Category category);
}
