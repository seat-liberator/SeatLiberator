package com.seatliberator.seatliberator.board.application.exception;

import java.util.UUID;

public class BoardNotFoundException extends RuntimeException {
    // 변경사항: "게시판 없음" 상황을 표현하는 전용 예외 타입 도입.
    // 이유:
    // 1) IllegalArgumentException 같은 범용 예외보다 의미가 명확함.
    // 2) 웹 계층에서 404로 매핑할 근거가 분명해짐.
    public BoardNotFoundException(UUID boardId) {
        super("Board not found: " + boardId);
    }
}
