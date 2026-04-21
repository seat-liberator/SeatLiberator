package com.seatliberator.seatliberator.board.infrastructure.web.controller;

import com.seatliberator.seatliberator.board.application.port.in.CategoryManager;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryUpdateCommand;
import com.seatliberator.seatliberator.board.infrastructure.web.request.CategoryCreateRequest;
import com.seatliberator.seatliberator.board.infrastructure.web.request.CategoryUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Tag(name = "Categories", description = "게시판 카테고리 관리 API")
@RequestMapping("/board/{boardId}/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryManager categoryManager;

    @Operation(summary = "카테고리 목록 조회", description = "특정 게시판에 속한 카테고리 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @GetMapping
    public ResponseEntity<?> getAll(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId
    ) {
        return ResponseEntity.ok(categoryManager.getAll(boardId));
    }

    @Operation(summary = "카테고리 단건 조회", description = "특정 게시판 안의 카테고리 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<?> get(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId,
            @Parameter(description = "카테고리 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e4")
            @PathVariable("categoryId") UUID categoryId
    ) {
        return ResponseEntity.ok(categoryManager.get(boardId, categoryId));
    }

    @Operation(summary = "카테고리 생성", description = "특정 게시판에 새 카테고리를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @PostMapping
    public ResponseEntity<?> post(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId,
            @RequestBody @Valid CategoryCreateRequest body
    ) {
        var command = new CategoryCreateCommand(boardId, body.name(), body.description());
        var result = categoryManager.create(command);
        return ResponseEntity
                .created(URI.create("/board/" + boardId + "/categories/" + result.categoryId()))
                .body(result);
    }

    @Operation(summary = "카테고리 정보 변경", description = "특정 게시판 안의 카테고리 이름과 설명을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @PatchMapping("/{categoryId}")
    public ResponseEntity<?> patch(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId,
            @Parameter(description = "카테고리 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e4")
            @PathVariable("categoryId") UUID categoryId,
            @RequestBody CategoryUpdateRequest body
    ) {
        var command = new CategoryUpdateCommand(boardId, categoryId, body.name(), body.description());
        return ResponseEntity.ok(categoryManager.update(command));
    }

    @Operation(summary = "카테고리 삭제", description = "특정 게시판 안의 카테고리를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> delete(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId,
            @Parameter(description = "카테고리 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e4")
            @PathVariable("categoryId") UUID categoryId
    ) {
        categoryManager.delete(new CategoryDeleteCommand(boardId, categoryId));
        return ResponseEntity.noContent().build();
    }
}
