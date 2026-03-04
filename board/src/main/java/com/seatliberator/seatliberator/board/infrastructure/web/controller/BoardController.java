package com.seatliberator.seatliberator.board.infrastructure.web.controller;

import com.seatliberator.seatliberator.board.application.port.in.BoardManager;
import com.seatliberator.seatliberator.board.infrastructure.web.request.BoardCreateRequest;
import com.seatliberator.seatliberator.board.infrastructure.web.request.BoardDeleteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/board")
@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardManager boardManager;

    @PostMapping
    public ResponseEntity<?> post(
            @RequestBody BoardCreateRequest body
    ) {
        var result = boardManager.create(body.name(), body.description());

        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(
            @RequestBody BoardDeleteRequest body
    ) {
        boardManager.remove(body.boardId());
        return ResponseEntity.noContent().build();
    }
}
