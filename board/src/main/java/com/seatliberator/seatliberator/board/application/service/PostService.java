package com.seatliberator.seatliberator.board.application.service;

import com.seatliberator.seatliberator.board.application.exception.BoardNotFoundException;
import com.seatliberator.seatliberator.board.application.exception.PostNotFoundException;
import com.seatliberator.seatliberator.board.application.port.in.PostEntry;
import com.seatliberator.seatliberator.board.application.port.in.PostManager;
import com.seatliberator.seatliberator.board.application.port.in.command.PostCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.PostDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.PostUpdateCommand;
import com.seatliberator.seatliberator.board.application.port.out.BoardStore;
import com.seatliberator.seatliberator.board.domain.Board;
import com.seatliberator.seatliberator.board.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements PostManager {
    // PostService는 게시글 유스케이스를 구현하는 애플리케이션 서비스
    // DDD 관점에서 Post는 Board 애그리거트 내부에서 관리됨...
    // Post 조작도 Board를 로드하고 Board의 행위로 처리함
    private final BoardStore boardStore;

    @Override
    @Transactional
    public PostEntry create(PostCreateCommand command) {
        // 1. 애그리거트 루트(Board) 조회
        var board = findBoardOrThrow(command.boardId());

        // 2. 필수 입력값 검증
        var title = Optional.ofNullable(command.title())
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("Post title is required."));
        var content = Optional.ofNullable(command.content())
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("Post content is required."));

        // 3. 도메인 행위로 게시글 생성 및 연관관계 설정
        var post = board.addPost(title, content);

        // 4. 애그리거트 저장인데 cascade 설정으로 Post까지 함께 반영
        boardStore.save(board);

        // 5. 외부에 노출할 읽기를 DTO로 변환
        return PostEntry.of(post);
    }

    @Override
    @Transactional
    public PostEntry update(PostUpdateCommand command) {
        // 1. 게시판/게시글 존재 확인하고
        var board = findBoardOrThrow(command.boardId());
        var post = findPostOrThrow(board, command.postId());

        // 2. PATCH semantics 값이 null이면 기존 값 유지
        var newTitle = Optional.ofNullable(command.title())
                .orElse(post.getTitle());
        var newContent = Optional.ofNullable(command.content())
                .orElse(post.getContent());

        // 3. 게시글 상태 변경
        post.setTitle(newTitle);
        post.setContent(newContent);
        boardStore.save(board);

        return PostEntry.of(post);
    }

    @Override
    @Transactional
    public void delete(PostDeleteCommand command) {
        // Board 애그리거트에서 Post를 제거한 다음에~
        // orphanRemoval=true 설정으로 연관이 끊긴 Post는 DB에서 함께 삭제됨
        var board = findBoardOrThrow(command.boardId());
        var post = findPostOrThrow(board, command.postId());
        board.removePost(post);
        boardStore.save(board);
    }

    @Override
    @Transactional(readOnly = true)
    public PostEntry get(UUID boardId, UUID postId) {
        // readOnly 트랜잭션~ 조회 전용 힌트(쓰기 의도 없음)
        var board = findBoardOrThrow(boardId);
        return PostEntry.of(findPostOrThrow(board, postId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostEntry> getAll(UUID boardId) {
        // 게시판 단위로 게시글 목록을 조회하고 응답 DTO로 매핑하기
        var board = findBoardOrThrow(boardId);
        return board.getPosts().stream()
                .map(PostEntry::of)
                .toList();
    }

    private Board findBoardOrThrow(UUID boardId) {
        // 게시판이 없으면 BoardNotFoundException -> ControllerAdvice에서 404로 변환
        return boardStore.getSingle(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }

    private Post findPostOrThrow(Board board, UUID postId) {
        // 게시글이 없으면 PostNotFoundException -> ControllerAdvice에서 404로 변환
        return board.findPost(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }
}
