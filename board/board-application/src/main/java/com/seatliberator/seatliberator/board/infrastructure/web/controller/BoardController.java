package com.seatliberator.seatliberator.board.infrastructure.web.controller;

import com.seatliberator.seatliberator.board.application.port.in.BoardManager;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardUpdateCommand;
import com.seatliberator.seatliberator.board.infrastructure.web.request.BoardCreateRequest;
import com.seatliberator.seatliberator.board.infrastructure.web.request.BoardUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Boards", description = "게시판 관리 API")
@RequestMapping("/board")
@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardManager boardManager;

    @Operation(summary = "게시판 목록 조회", description = "등록된 게시판 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(boardManager.getAll());
    }

    @Operation(summary = "게시판 단건 조회", description = "게시판 ID로 게시판 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @GetMapping("/{boardId}")
    public ResponseEntity<?> get(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId
    ) {
        return ResponseEntity.ok(boardManager.get(boardId));
    }

    @Operation(summary = "게시판 생성", description = "새 게시판을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
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

    @Operation(summary = "게시판 정보 변경", description = "기존 게시판의 이름과 설명을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @PatchMapping("/{boardId}")
    public ResponseEntity<?> patch(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
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

    @Operation(summary = "게시판 삭제", description = "기존 게시판을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> delete(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId
    ) {
        var command = new BoardDeleteCommand(
                boardId
        );

        boardManager.delete(command);

        return ResponseEntity.noContent().build();
    }
}
