package com.seatliberator.seatliberator.board.infrastructure.web.controller;

import com.seatliberator.seatliberator.board.application.port.in.PostManager;
import com.seatliberator.seatliberator.board.application.port.in.command.PostCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.PostDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.PostUpdateCommand;
import com.seatliberator.seatliberator.board.infrastructure.web.request.PostCreateRequest;
import com.seatliberator.seatliberator.board.infrastructure.web.request.PostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RequestMapping("/board/{boardId}/posts")
@RestController
@RequiredArgsConstructor
public class PostController {
    // PostManager는 게시글 유스케이스의 진입점
    // 컨트롤러는 비즈니스 로직을 직접 처리하지 않고 포트에 위임함
    private final PostManager postManager;

    // GET /board/{boardId}/posts
    // 특정 게시판에 속한 게시글 목록을 조회
    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable("boardId") UUID boardId) {
        return ResponseEntity.ok(postManager.getAll(boardId));
    }

    // GET /board/{boardId}/posts/{postId}
    // 특정 게시판 안의 특정 게시글 1건을 조회
    // boardId와 postId를 함께 받는 이유는 어떤 게시판의 게시글인지 명확히 알기 이ㅜ해
    @GetMapping("/{postId}")
    public ResponseEntity<?> get(
            @PathVariable("boardId") UUID boardId,
            @PathVariable("postId") UUID postId
    ) {
        return ResponseEntity.ok(postManager.get(boardId, postId));
    }

    // POST /board/{boardId}/posts
    // 요청 JSON을 서비스가 이해하는 커맨드로 변환해서 전달
    // 생성 성공하면 201 Created + Location 헤더를 반환
    @PostMapping
    public ResponseEntity<?> post(
            @PathVariable("boardId") UUID boardId,
            @RequestBody @Valid PostCreateRequest body
    ) {
        // Web DTO -> Application Command 변환
        var command = new PostCreateCommand(boardId, body.title(), body.content());
        var result = postManager.create(command);

        // 생성된 리소스 URI를 Location 헤더로 제공
        return ResponseEntity.created(URI.create("/board/" + boardId + "/posts/" + result.postId())).body(result);
    }

    // PATCH /board/{boardId}/posts/{postId}
    // 부분 수정 API인데 title/content 중 일부만 보내도 됨 null이면 기존값유지
    @PatchMapping("/{postId}")
    public ResponseEntity<?> patch(
            @PathVariable("boardId") UUID boardId,
            @PathVariable("postId") UUID postId,
            @RequestBody PostUpdateRequest body
    ) {
        // Web DTO 에서 Application Command 로 변환
        var command = new PostUpdateCommand(boardId, postId, body.title(), body.content());
        return ResponseEntity.ok(postManager.update(command));
    }

    // DELETE /board/{boardId}/posts/{postId}
    // 삭제 성공 시 응답 없이 204 No Content 반환함
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(
            @PathVariable("boardId") UUID boardId,
            @PathVariable("postId") UUID postId
    ) {
        postManager.delete(new PostDeleteCommand(boardId, postId));
        return ResponseEntity.noContent().build();
    }
}
