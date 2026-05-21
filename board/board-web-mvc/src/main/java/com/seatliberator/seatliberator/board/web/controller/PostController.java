package com.seatliberator.seatliberator.board.web.controller;

import com.seatliberator.seatliberator.board.application.port.in.PostManager;
import com.seatliberator.seatliberator.board.application.port.in.command.PostCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.PostDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.PostUpdateCommand;
import com.seatliberator.seatliberator.board.web.request.PostCreateRequest;
import com.seatliberator.seatliberator.board.web.request.PostUpdateRequest;
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

@Tag(name = "Posts", description = "게시판 게시글 관리 API")
@RequestMapping("/board/{boardId}/posts")
@RestController
@RequiredArgsConstructor
public class PostController {
    // PostManager는 게시글 유스케이스의 진입점
    // 컨트롤러는 비즈니스 로직을 직접 처리하지 않고 포트에 위임함
    private final PostManager postManager;

    // GET /board/{boardId}/posts
    // 특정 게시판에 속한 게시글 목록을 조회
    @Operation(summary = "게시글 목록 조회", description = "특정 게시판에 속한 게시글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시판 없음")
    })
    @GetMapping
    public ResponseEntity<?> getAll(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId
    ) {
        return ResponseEntity.ok(postManager.getAll(boardId));
    }

    // GET /board/{boardId}/posts/{postId}
    // 특정 게시판 안의 특정 게시글 1건을 조회
    // boardId와 postId를 함께 받는 이유는 어떤 게시판의 게시글인지 명확히 알기 이ㅜ해
    @Operation(summary = "게시글 단건 조회", description = "특정 게시판 안의 게시글 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<?> get(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId,
            @Parameter(description = "게시글 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e5")
            @PathVariable("postId") UUID postId
    ) {
        return ResponseEntity.ok(postManager.get(boardId, postId));
    }

    // POST /board/{boardId}/posts
    // 요청 JSON을 서비스가 이해하는 커맨드로 변환해서 전달
    // 생성 성공하면 201 Created + Location 헤더를 반환
    @Operation(summary = "게시글 생성", description = "특정 게시판에 새 게시글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시판 또는 카테고리 없음")
    })
    @PostMapping
    public ResponseEntity<?> post(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId,
            @RequestBody @Valid PostCreateRequest body
    ) {
        // Web DTO -> Application Command 변환
        var command = new PostCreateCommand(boardId, body.categoryId(), body.title(), body.content());
        var result = postManager.create(command);

        // 생성된 리소스 URI를 Location 헤더로 제공
        return ResponseEntity.created(URI.create("/board/" + boardId + "/posts/" + result.postId())).body(result);
    }

    // PATCH /board/{boardId}/posts/{postId}
    // 부분 수정 API인데 title/content 중 일부만 보내도 됨 null이면 기존값유지
    @Operation(summary = "게시글 정보 변경", description = "특정 게시판 안의 게시글 제목, 내용, 카테고리를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PatchMapping("/{postId}")
    public ResponseEntity<?> patch(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId,
            @Parameter(description = "게시글 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e5")
            @PathVariable("postId") UUID postId,
            @RequestBody PostUpdateRequest body
    ) {
        // Web DTO 에서 Application Command 로 변환
        var command = new PostUpdateCommand(boardId, postId, body.categoryId(), body.title(), body.content());
        return ResponseEntity.ok(postManager.update(command));
    }

    // DELETE /board/{boardId}/posts/{postId}
    // 삭제 성공 시 응답 없이 204 No Content 반환함
    @Operation(summary = "게시글 삭제", description = "특정 게시판 안의 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(
            @Parameter(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable("boardId") UUID boardId,
            @Parameter(description = "게시글 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e5")
            @PathVariable("postId") UUID postId
    ) {
        postManager.delete(new PostDeleteCommand(boardId, postId));
        return ResponseEntity.noContent().build();
    }
}
