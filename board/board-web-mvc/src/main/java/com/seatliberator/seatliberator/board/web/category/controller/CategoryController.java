package com.seatliberator.seatliberator.board.web.category.controller;

import com.seatliberator.seatliberator.board.application.category.port.in.*;
import com.seatliberator.seatliberator.board.application.category.port.in.command.DeleteCategoryCommand;
import com.seatliberator.seatliberator.board.application.category.port.in.query.FindCategoryQuery;
import com.seatliberator.seatliberator.board.application.category.port.in.query.ListCategoryQuery;
import com.seatliberator.seatliberator.board.application.category.port.in.result.CategoryResult;
import com.seatliberator.seatliberator.board.web.category.request.CreateCategoryRequest;
import com.seatliberator.seatliberator.board.web.category.request.UpdateCategoryDescriptionRequest;
import com.seatliberator.seatliberator.board.web.category.request.UpdateCategoryNameRequest;
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

@Tag(name = "Categories", description = "게시판 카테고리 관리 API")
@RequestMapping("/api/v1/board/{boardId}/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryNameUseCase updateCategoryNameUseCase;
    private final UpdateCategoryDescriptionUseCase updateCategoryDescriptionUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    private final FindCategoryUseCase findCategoryUseCase;
    private final ListCategoryUseCase listCategoryUseCase;

    @Operation(summary = "카테고리 목록 조회", description = "특정 게시판에 속한 카테고리 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResult>> listCategory(
            @Parameter(description = "게시판 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("boardId") UUID boardId,
            @Parameter(description = "카테고리 이름", example = "질문")
            @RequestParam(name = "name", required = false) String name,
            @Parameter(description = "카테고리 설명", example = "질문 글을 작성하는 카테고리입니다.")
            @RequestParam(name = "description", required = false) String description
    ) {
        var query = ListCategoryQuery.of(boardId, name, description);
        var result = listCategoryUseCase.list(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "카테고리 단건 조회", description = "특정 게시판 안의 카테고리 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResult> findCategory(
            @Parameter(description = "카테고리 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("categoryId") UUID categoryId
    ) {
        var query = FindCategoryQuery.of(categoryId);
        var result = findCategoryUseCase.find(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "카테고리 생성", description = "특정 게시판에 새 카테고리를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @PostMapping
    public ResponseEntity<CategoryResult> createCategory(
            @Parameter(description = "게시판 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("boardId") UUID boardId,
            @RequestBody @Valid CreateCategoryRequest request
    ) {
        var command = request.toCommand(boardId);
        var result = createCategoryUseCase.create(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "카테고리 이름 변경", description = "특정 게시판 안의 카테고리 이름을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @PatchMapping("/{categoryId}/name")
    public ResponseEntity<CategoryResult> updateCategoryName(
            @Parameter(description = "카테고리 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("categoryId") UUID categoryId,
            @RequestBody @Valid UpdateCategoryNameRequest request
    ) {
        var command = request.toCommand(categoryId);
        var result = updateCategoryNameUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "카테고리 설명 변경", description = "특정 게시판 안의 카테고리 설명을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @PatchMapping("/{categoryId}/description")
    public ResponseEntity<CategoryResult> updateCategoryDescription(
            @Parameter(description = "카테고리 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("categoryId") UUID categoryId,
            @RequestBody @Valid UpdateCategoryDescriptionRequest request
    ) {
        var command = request.toCommand(categoryId);
        var result = updateCategoryDescriptionUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "카테고리 삭제", description = "특정 게시판 안의 카테고리를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> delete(
            @Parameter(description = "카테고리 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("categoryId") UUID categoryId
    ) {
        var command = DeleteCategoryCommand.of(categoryId);
        deleteCategoryUseCase.delete(command);
        return ResponseEntity.noContent().build();
    }
}
