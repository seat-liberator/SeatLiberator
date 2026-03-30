package com.seatliberator.seatliberator.board.infrastructure.web.controller;

import com.seatliberator.seatliberator.board.application.port.in.CategoryManager;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryUpdateCommand;
import com.seatliberator.seatliberator.board.infrastructure.web.request.CategoryCreateRequest;
import com.seatliberator.seatliberator.board.infrastructure.web.request.CategoryUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RequestMapping("/board/{boardId}/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryManager categoryManager;

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable("boardId") UUID boardId) {
        return ResponseEntity.ok(categoryManager.getAll(boardId));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> get(
            @PathVariable("boardId") UUID boardId,
            @PathVariable("categoryId") UUID categoryId
    ) {
        return ResponseEntity.ok(categoryManager.get(boardId, categoryId));
    }

    @PostMapping
    public ResponseEntity<?> post(
            @PathVariable("boardId") UUID boardId,
            @RequestBody @Valid CategoryCreateRequest body
    ) {
        var command = new CategoryCreateCommand(boardId, body.name(), body.description());
        var result = categoryManager.create(command);
        return ResponseEntity
                .created(URI.create("/board/" + boardId + "/categories/" + result.categoryId()))
                .body(result);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<?> patch(
            @PathVariable("boardId") UUID boardId,
            @PathVariable("categoryId") UUID categoryId,
            @RequestBody CategoryUpdateRequest body
    ) {
        var command = new CategoryUpdateCommand(boardId, categoryId, body.name(), body.description());
        return ResponseEntity.ok(categoryManager.update(command));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> delete(
            @PathVariable("boardId") UUID boardId,
            @PathVariable("categoryId") UUID categoryId
    ) {
        categoryManager.delete(new CategoryDeleteCommand(boardId, categoryId));
        return ResponseEntity.noContent().build();
    }
}
