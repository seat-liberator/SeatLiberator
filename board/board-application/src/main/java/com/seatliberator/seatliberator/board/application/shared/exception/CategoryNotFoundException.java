package com.seatliberator.seatliberator.board.application.shared.exception;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(UUID categoryId) {
        super("Category not found: " + categoryId);
    }
}
