package com.seatliberator.seatliberator.board.application.port.out;

import com.seatliberator.seatliberator.board.domain.Board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardStore {
    void save(Board board);

    void remove(UUID boardId);

    Optional<Board> getSingle(UUID boardId);

    // 변경사항: 저장소 포트에 전체 게시판 조회 기능을 추가
    // 이유: 서비스 계층이 JPA 구현체 세부사항에 의존하지 않고 목록 조회를 수행하도록 포트에서 추상화해야 함
    List<Board> getAll();
}
