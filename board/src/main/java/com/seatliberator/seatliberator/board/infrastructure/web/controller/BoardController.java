package com.seatliberator.seatliberator.board.infrastructure.web.controller;

import com.seatliberator.seatliberator.board.application.port.in.BoardManager;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardDeleteCommand;
import com.seatliberator.seatliberator.board.infrastructure.web.request.BoardCreateRequest;
import com.seatliberator.seatliberator.board.infrastructure.web.request.BoardDeleteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/board")
@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardManager boardManager;

    @PostMapping
    public ResponseEntity<?> post(
            @RequestBody BoardCreateRequest body
    ) {
        var command = new BoardCreateCommand(
                body.name(),
                body.description()
        );

        var result = boardManager.create(command);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> delete(
            @PathVariable("boardId") UUID boardId,
            @RequestBody BoardDeleteRequest body
    ) {
        var command = new BoardDeleteCommand(
                boardId
        );

        boardManager.delete(command);

        return ResponseEntity.noContent().build();
    }
}
