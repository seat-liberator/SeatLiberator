package com.seatliberator.seatliberator.board.web.comment.controller;

import com.seatliberator.seatliberator.board.application.comment.port.in.*;
import com.seatliberator.seatliberator.board.application.comment.port.in.command.DeleteCommentCommand;
import com.seatliberator.seatliberator.board.application.comment.port.in.query.FindCommentQuery;
import com.seatliberator.seatliberator.board.application.comment.port.in.query.ListPostCommentQuery;
import com.seatliberator.seatliberator.board.application.comment.port.in.result.CommentResult;
import com.seatliberator.seatliberator.board.web.comment.request.CreateCommentRequest;
import com.seatliberator.seatliberator.board.web.comment.request.UpdateCommentContentRequest;
import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
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

@Tag(name = "Comments", description = "게시글 댓글 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/board/{boardId}/posts/{postId}/comments")
public class CommentController {
    private final CreateCommentUseCase createCommentUseCase;
    private final UpdateCommentContentUseCase updateCommentContentUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;

    private final FindCommentUseCase findCommentUseCase;
    private final ListPostCommentUseCase listPostCommentUseCase;

    private final ActorContextHolder actorContextHolder;

    @Operation(summary = "게시글 댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @GetMapping
    public ResponseEntity<List<CommentResult>> listComment(
            @Parameter(description = "게시글 ID", example = "00000000-0000-0000-000000000001")
            @PathVariable("postId") UUID postId
    ) {
        var query = ListPostCommentQuery.of(postId);
        var result = listPostCommentUseCase.list(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "게시글 댓글 단건 조회", description = "특정 게시글의 댓글 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResult> findComment(
            @Parameter(description = "댓글 ID", example = "00000000-0000-0000-000000000001")
            @PathVariable("commentId") UUID commentId
    ) {
        var query = FindCommentQuery.of(commentId);
        var result = findCommentUseCase.find(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "댓글 생성", description = "특정 게시글에 댓글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PostMapping
    public ResponseEntity<CommentResult> createComment(
            @Parameter(description = "게시글 ID", example = "00000000-0000-0000-000000000001")
            @PathVariable("postId") UUID postId,
            @RequestBody @Valid CreateCommentRequest request
    ) {
        var actor = actorContextHolder.getActor();
        var command = request.toCommand(postId, actor.subject());
        var result = createCommentUseCase.create(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "댓글 본문 변경", description = "특정 댓글 본문을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @PatchMapping("/{commentId}/content")
    public ResponseEntity<CommentResult> updateCommentContent(
            @Parameter(description = "댓글 ID", example = "00000000-0000-0000-000000000001")
            @PathVariable("commentId") UUID commentId,
            @RequestBody @Valid UpdateCommentContentRequest request
    ) {
        var command = request.toCommand(commentId);
        var result = updateCommentContentUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 ID", example = "00000000-0000-0000-000000000001")
            @PathVariable("commentId") UUID commentId
    ) {
        var command = DeleteCommentCommand.of(commentId);
        deleteCommentUseCase.delete(command);
        return ResponseEntity.noContent().build();
    }
}
