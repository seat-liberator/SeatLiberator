package com.seatliberator.seatliberator.reservation.book.application.exception;

import com.seatliberator.seatliberator.kernel.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookApplicationErrorCode implements ErrorCode {
    RESERVATION_NOT_FOUND("R_001", "예약 정보가 존재하지 않습니다."),
    SEAT_NOT_FOUND("R_002", "좌석 정보가 존재하지 않습니다."),
    RESERVATION_ALREADY_EXISTS("R_003", "예약은 두 개 이상 할 수 없습니다."),
    RESERVATION_TIME_CONFLICT("R_004", "다른 예약과 겹치는 시간으로 예약할 수 없습니다.");

    private final String code;
    private final String message;
}
