package com.seatliberator.seatliberator.board.web.post.controller;

import com.seatliberator.seatliberator.board.application.post.port.in.*;
import com.seatliberator.seatliberator.board.application.post.port.in.command.DeletePostCommand;
import com.seatliberator.seatliberator.board.application.post.port.in.query.FindPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.query.ListBoardPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.result.BoardPostSummaryResult;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;
import com.seatliberator.seatliberator.board.web.post.request.CreatePostRequest;
import com.seatliberator.seatliberator.board.web.post.request.UpdatePostCategoryRequest;
import com.seatliberator.seatliberator.board.web.post.request.UpdatePostContentRequest;
import com.seatliberator.seatliberator.board.web.post.request.UpdatePostTitleRequest;
import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
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

@Tag(name = "Posts", description = "게시판 게시글 관리 API")
@RequestMapping("/api/v1/board/{boardId}/posts")
@RestController
@RequiredArgsConstructor
public class PostController {
    private final CreatePostUseCase createPostUseCase;
    private final UpdatePostCategoryUseCase updatePostCategoryUseCase;
    private final UpdatePostTitleUseCase updatePostTitleUseCase;
    private final UpdatePostContentUseCase updatePostContentUseCase;
    private final DeletePostUseCase deletePostUseCase;

    private final FindPostUseCase findPostUseCase;
    private final ListBoardPostUseCase listBoardPostUseCase;

    private final ActorContextHolder actorContextHolder;

    @Operation(summary = "게시판 내 게시글 목록 조회", description = "특정 게시판 내 게시글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @GetMapping
    public ResponseEntity<List<BoardPostSummaryResult>> listPostInBoard(
            @Parameter(description = "게시판 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("boardId") UUID boardId,
            @Parameter(description = "카테고리 ID", example = "00000000-0000-0000-0000-000000000001")
            @RequestParam(name = "categoryId", required = false) UUID categoryId
    ) {
        var query = ListBoardPostQuery.of(boardId, categoryId);
        var result = listBoardPostUseCase.list(query);
        return ResponseEntity.ok(result);
    }

    // GET /api/v1/board/{boardId}/posts/{postId}
    // 특정 게시판 안의 특정 게시글 1건을 조회
    // boardId와 postId를 함께 받는 이유는 어떤 게시판의 게시글인지 명확히 알기 이ㅜ해 <- ??
    @Operation(summary = "게시글 단건 조회", description = "특정 게시판 안의 게시글 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostResult> findPost(
            @Parameter(description = "게시글 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("postId") UUID postId
    ) {
        var query = FindPostQuery.of(postId);
        var result = findPostUseCase.find(query);
        return ResponseEntity.ok(result);
    }

    // POST /api/v1/board/{boardId}/posts
    // 요청 JSON을 서비스가 이해하는 커맨드로 변환해서 전달
    // 생성 성공하면 201 Created + Location 헤더를 반환
    @Operation(summary = "게시글 생성", description = "특정 게시판에 새 게시글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시판 또는 카테고리 없음")
    })
    @PostMapping
    public ResponseEntity<PostResult> createPost(
            @Parameter(description = "게시판 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("boardId") UUID boardId,
            @RequestBody @Valid CreatePostRequest request
    ) {
        var actor = actorContextHolder.getActor();
        var command = request.toCommand(boardId, actor.subject());
        var result = createPostUseCase.create(command);
        return ResponseEntity.ok(result);
    }

    // PATCH /api/v1/board/{boardId}/posts/{postId}/title
    @Operation(summary = "게시글 제목 변경", description = "특정 게시판 안의 게시글 제목을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PatchMapping("/{postId}/title")
    public ResponseEntity<PostResult> updatePostTitle(
            @Parameter(description = "게시글 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("postId") UUID postId,
            @RequestBody @Valid UpdatePostTitleRequest request
    ) {
        var command = request.toCommand(postId);
        var result = updatePostTitleUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    // PATCH /api/v1/board/{boardId}/posts/{postId}/content
    @Operation(summary = "게시글 내용 변경", description = "특정 게시판 안의 게시글 내용을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PatchMapping("/{postId}/content")
    public ResponseEntity<PostResult> updatePostContent(
            @Parameter(description = "게시글 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("postId") UUID postId,
            @RequestBody @Valid UpdatePostContentRequest request
    ) {
        var command = request.toCommand(postId);
        var result = updatePostContentUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    // PATCH /api/v1/board/{boardId}/posts/{postId}/category
    @Operation(summary = "게시글 카테고리 변경", description = "특정 게시판 안의 게시글 카테고리를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PatchMapping("/{postId}/category")
    public ResponseEntity<PostResult> patch(
            @Parameter(description = "게시글 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("postId") UUID postId,
            @RequestBody @Valid UpdatePostCategoryRequest request
    ) {
        var command = request.toCommand(postId);
        var result = updatePostCategoryUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    // DELETE /api/v1/board/{boardId}/posts/{postId}
    // 삭제 성공 시 응답 없이 204 No Content 반환함
    @Operation(summary = "게시글 삭제", description = "특정 게시판 안의 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(
            @Parameter(description = "게시판 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("boardId") UUID boardId,
            @Parameter(description = "게시글 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("postId") UUID postId
    ) {
        var command = DeletePostCommand.of(postId);
        deletePostUseCase.delete(command);
        return ResponseEntity.noContent().build();
    }
}
