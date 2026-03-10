package com.seatliberator.seatliberator.board.infrastructure.web.controller;

import com.seatliberator.seatliberator.board.application.exception.BoardNotFoundException;
import com.seatliberator.seatliberator.board.application.exception.PostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BoardControllerAdvice {
    // 변경사항: BoardNotFoundException -> 404 ProblemDetail 매핑 추가.
    // 이유:
    // 1) 서비스 계층 예외를 HTTP 표준 응답으로 일관되게 변환하기 위함.
    // 2) 클라이언트가 에러를 구조화된 형태(ProblemDetail)로 처리할 수 있게 하기 위함.
    @ExceptionHandler(BoardNotFoundException.class)
    public ProblemDetail handleBoardNotFound(BoardNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ProblemDetail handlePostNotFound(PostNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
