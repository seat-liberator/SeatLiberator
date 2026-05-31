package com.seatliberator.seatliberator.board.web.user.controller;

import com.seatliberator.seatliberator.board.application.comment.port.in.ListUserCommentUseCase;
import com.seatliberator.seatliberator.board.application.comment.port.in.query.ListUserCommentQuery;
import com.seatliberator.seatliberator.board.application.comment.port.in.result.CommentResult;
import com.seatliberator.seatliberator.board.application.post.port.in.ListUserPostUseCase;
import com.seatliberator.seatliberator.board.application.post.port.in.query.ListUserPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board/users")
public class UserController {
    private final ListUserPostUseCase listUserPostUseCase;
    private final ListUserCommentUseCase listUserCommentUseCase;

    @Operation(summary = "사용자 게시글 목록 조회", description = "특정 사용자의 게시글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<PostResult>> listUserPost(
            @Parameter(description = "사용자 ID", example = "test_user")
            @PathVariable("userId") String userId
    ) {
        var query = ListUserPostQuery.of(userId);
        var result = listUserPostUseCase.list(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "사용자 댓글 목록 조회", description = "특정 사용자의 댓글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping("/{userId}/comments")
    public ResponseEntity<List<CommentResult>> listUserComment(
            @Parameter(description = "사용자 Id", example = "test_user")
            @PathVariable("userId") String userId
    ) {
        var query = ListUserCommentQuery.of(userId);
        var result = listUserCommentUseCase.list(query);
        return ResponseEntity.ok(result);
    }
}
