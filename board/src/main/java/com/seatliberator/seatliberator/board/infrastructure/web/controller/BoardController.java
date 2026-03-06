package com.seatliberator.seatliberator.board.infrastructure.web.controller;

import com.seatliberator.seatliberator.board.application.port.in.BoardManager;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardUpdateCommand;
import com.seatliberator.seatliberator.board.infrastructure.web.request.BoardCreateRequest;
import com.seatliberator.seatliberator.board.infrastructure.web.request.BoardUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/board")
@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardManager boardManager;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(boardManager.getAll());
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<?> get(
            @PathVariable("boardId") UUID boardId
    ) {
        return ResponseEntity.ok(boardManager.get(boardId));
    }

    @PostMapping
    public ResponseEntity<?> post(
            @RequestBody @Valid BoardCreateRequest body
    ) {
        var command = new BoardCreateCommand(
                body.name(),
                body.description()
        );

        var result = boardManager.create(command);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<?> patch(
            @PathVariable("boardId") UUID boardId,
            @RequestBody BoardUpdateRequest body
    ) {
        var command = new BoardUpdateCommand(
                boardId,
                body.name(),
                body.description()
        );

        var result = boardManager.update(command);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> delete(
            @PathVariable("boardId") UUID boardId
    ) {
        var command = new BoardDeleteCommand(
                boardId
        );

        boardManager.delete(command);

        return ResponseEntity.noContent().build();
    }
}
