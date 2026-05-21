package com.seatliberator.seatliberator.board.application.category.port.out;

import com.seatliberator.seatliberator.board.domain.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryReader {
    boolean existsById(UUID id);

    Optional<Category> findById(UUID id);
}
