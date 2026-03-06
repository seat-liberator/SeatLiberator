package com.seatliberator.seatliberator.board.application.service;

import com.seatliberator.seatliberator.board.application.exception.BoardNotFoundException;
import com.seatliberator.seatliberator.board.application.port.in.BoardEntry;
import com.seatliberator.seatliberator.board.application.port.in.BoardManager;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardUpdateCommand;
import com.seatliberator.seatliberator.board.application.port.out.BoardStore;
import com.seatliberator.seatliberator.board.domain.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService implements BoardManager {
    private final BoardStore boardStore;

    @Override
    public BoardEntry create(BoardCreateCommand command) {
        // 변경사항: 이름(name) 필수값을 서비스에서 명시적으로 검증.
        // 이유:
        // 1) DB의 not-null 제약에만 의존하면 예외가 영속 계층에서 늦게 발생해 원인 파악이 어려움.
        // 2) 애플리케이션 계층에서 비즈니스 전제조건을 먼저 확인하는 것이 책임 분리에 더 적합함.
        var boardName = Optional.ofNullable(command.name())
                .filter(name -> !name.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("Board name is required."));
        var board = Board.create(boardName, command.description());
        boardStore.save(board);
        return BoardEntry.of(board);
    }

    @Override
    public BoardEntry update(BoardUpdateCommand command) {
        // 변경사항: 조회 실패 시 IllegalArgumentException 대신 도메인 맥락의 전용 예외(BoardNotFoundException) 사용.
        // 이유:
        // 1) 예외 타입만으로 "리소스 없음(404)" 상황을 정확히 구분 가능.
        // 2) ControllerAdvice에서 일관된 HTTP 응답 매핑이 쉬워짐.
        var board = findByIdOrThrow(command.boardId());

        // 변경사항: null 전달 시 기존 값 유지(부분 수정, PATCH semantics).
        // 이유: 클라이언트가 일부 필드만 보내도 안전하게 업데이트 가능하도록 하기 위함.
        var newBoardName = Optional.ofNullable(command.name())
                .orElse(board.getName());
        var newBoardDescription = Optional.ofNullable(command.description())
                .orElse(board.getDescription());

        board.setName(newBoardName);
        board.setDescription(newBoardDescription);

        boardStore.save(board);

        return BoardEntry.of(board);
    }

    @Override
    public void delete(BoardDeleteCommand command) {
        var boardId = command.boardId();
        // 변경사항: 삭제 전 존재 여부를 먼저 확인.
        // 이유: 존재하지 않는 리소스 삭제 요청을 정상 처리(204)로 숨기지 않고, 명확히 404로 응답하기 위함.
        findByIdOrThrow(boardId);
        boardStore.remove(boardId);
    }

    @Override
    public BoardEntry get(UUID boardId) {
        // 변경사항: 단건 조회 유스케이스 구현.
        // 이유: 컨트롤러의 GET /board/{boardId} 엔드포인트를 서비스 포트로 연결.
        return BoardEntry.of(findByIdOrThrow(boardId));
    }

    @Override
    public List<BoardEntry> getAll() {
        // 변경사항: 목록 조회 유스케이스 구현.
        // 이유: 읽기 모델(BoardEntry)로 변환해 외부 계층에 도메인 엔티티를 직접 노출하지 않기 위함.
        return boardStore.getAll().stream()
                .map(BoardEntry::of)
                .toList();
    }

    private Board findByIdOrThrow(UUID boardId) {
        // 변경사항: "없음" 처리 로직을 공통 메서드로 추출.
        // 이유: get/update/delete에서 동일한 조회 실패 처리 정책을 중복 없이 재사용하기 위함.
        return boardStore.getSingle(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }
}
