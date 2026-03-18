package com.seatliberator.seatliberator.reservation.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationErrorCode {
    RESERVATION_NOT_FOUND("R_001", "예약 정보가 존재하지 않습니다."),
    SEAT_NOT_FOUND("R_002", "좌석 정보가 존재하지 않습니다.");

    private final String code;
    private final String message;
}
