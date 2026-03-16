package com.seatliberator.seatliberator.board.application.port.in;

import com.seatliberator.seatliberator.board.application.port.in.command.BoardCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardDeleteCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardUpdateCommand;

import java.util.List;
import java.util.UUID;

public interface BoardManager {
    BoardEntry create(BoardCreateCommand command);

    BoardEntry update(BoardUpdateCommand command);

    void delete(BoardDeleteCommand command);

    // 변경사항: 게시판 API의 읽기(Read) 요구를 충족하기 위해 단건 조회 유스케이스를 추가.
    // 이유: 기존 인터페이스는 create/update/delete만 제공하여 생성 후 조회, 상세 페이지 조회를 지원할 수 없었음.
    BoardEntry get(UUID boardId);

    // 변경사항: 게시판 목록 화면을 위한 전체 조회 유스케이스를 추가.
    // 이유: 컨트롤러에서 GET /board를 제공하려면 애플리케이션 포트에서 목록 반환 계약이 필요함.
    List<BoardEntry> getAll();
}
