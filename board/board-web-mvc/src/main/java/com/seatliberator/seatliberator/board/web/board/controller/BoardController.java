package com.seatliberator.seatliberator.board.web.board.controller;

import com.seatliberator.seatliberator.board.application.board.port.in.*;
import com.seatliberator.seatliberator.board.application.board.port.in.command.DeleteBoardCommand;
import com.seatliberator.seatliberator.board.application.board.port.in.query.FindBoardQuery;
import com.seatliberator.seatliberator.board.application.board.port.in.query.ListBoardQuery;
import com.seatliberator.seatliberator.board.application.board.port.in.result.BoardResult;
import com.seatliberator.seatliberator.board.web.board.request.CreateBoardRequest;
import com.seatliberator.seatliberator.board.web.board.request.UpdateBoardDescriptionRequest;
import com.seatliberator.seatliberator.board.web.board.request.UpdateBoardNameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Boards", description = "게시판 관리 API")
@RequestMapping("/api/v1/board")
@RestController
@RequiredArgsConstructor
public class BoardController {
    private final CreateBoardUseCase createBoardUseCase;
    private final UpdateBoardNameUseCase updateBoardNameUseCase;
    private final UpdateBoardDescriptionUseCase updateBoardDescriptionUseCase;
    private final DeleteBoardUseCase deleteBoardUseCase;

    private final FindBoardUseCase findBoardUseCase;
    private final ListBoardUseCase listBoardUseCase;

    @Operation(summary = "게시판 목록 조회", description = "등록된 게시판 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<BoardResult>> listBoard(
            @Parameter(description = "조회할 게시판 이름", example = "자유 게시판")
            @RequestParam(name = "name", required = false) String name,
            @Parameter(description = "조회할 게시판 설명", example = "자유롭게 글을 작성하는 게시판입니다.")
            @RequestParam(name = "description", required = false) String description
    ) {
        var query = ListBoardQuery.of(name, description);
        var result = listBoardUseCase.list(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "게시판 단건 조회", description = "게시판 ID로 게시판 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResult> findBoard(
            @Parameter(description = "게시판 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("boardId") UUID boardId
    ) {
        var query = FindBoardQuery.of(boardId);
        var result = findBoardUseCase.find(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "게시판 생성", description = "새 게시판을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<BoardResult> createBoard(
            @RequestBody @Valid CreateBoardRequest request
    ) {
        var command = request.toCommand();
        var result = createBoardUseCase.create(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "게시판 이름 변경", description = "기존 게시판의 이름을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @PatchMapping("/{boardId}/name")
    public ResponseEntity<BoardResult> updateBoardName(
            @Parameter(description = "게시판 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("boardId") UUID boardId,
            @RequestBody @Valid UpdateBoardNameRequest request
    ) {
        var command = request.toCommand(boardId);
        var result = updateBoardNameUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "게시판 설명 변경", description = "기존 게시판의 설명을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @PatchMapping("/{boardId}/description")
    public ResponseEntity<BoardResult> updateBoardDescription(
            @Parameter(description = "게시판 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("boardId") UUID boardId,
            @RequestBody @Valid UpdateBoardDescriptionRequest request
    ) {
        var command = request.toCommand(boardId);
        var result = updateBoardDescriptionUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "게시판 삭제", description = "기존 게시판을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @Parameter(description = "게시판 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("boardId") UUID boardId
    ) {
        var command = DeleteBoardCommand.of(boardId);
        deleteBoardUseCase.delete(command);
        return ResponseEntity.noContent().build();
    }
}
