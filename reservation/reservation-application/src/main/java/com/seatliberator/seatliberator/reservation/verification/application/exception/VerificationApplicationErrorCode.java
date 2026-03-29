package com.seatliberator.seatliberator.reservation.verification.application.exception;

import com.seatliberator.seatliberator.kernel.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerificationApplicationErrorCode implements ErrorCode {
    RESERVATION_READ_FORBIDDEN("RP001", "조회할 수 없는 예약입니다."),
    RESERVATION_VERIFY_FORBIDDEN("RP002", "사용 처리 할 수 없는 예약입니다."),
    RESERVATION_USAGE_FORBIDDEN("RP003", "사용할 수 없는 예약입니다."),
    RESERVATION_ALREADY_USED("RP004", "이미 사용 처리된 예약입니다."),
    RESERVATION_EXPIRED("RP005", "만료된 예약입니다.");

    private final String code;
    private final String message;
}
