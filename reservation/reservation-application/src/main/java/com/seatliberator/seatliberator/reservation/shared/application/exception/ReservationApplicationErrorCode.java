package com.seatliberator.seatliberator.reservation.shared.application.exception;

import com.seatliberator.seatliberator.kernel.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationApplicationErrorCode implements ErrorCode {
    RESERVATION_NOT_FOUND("RS001", "예약 정보가 존재하지 않습니다."),
    SEAT_NOT_FOUND("RS002", "좌석 정보가 존재하지 않습니다."),
    RESERVATION_ALREADY_EXISTS("RS003", "예약은 두 개 이상 할 수 없습니다."),
    RESERVATION_TIME_CONFLICT("RS004", "다른 예약과 겹치는 시간으로 예약할 수 없습니다."),
    SEAT_ALREADY_EXISTS("RS005", "좌석이 이미 존재합니다."),

    RESERVATION_READ_FORBIDDEN("RS100", "조회할 수 없는 예약입니다."),
    RESERVATION_VERIFY_FORBIDDEN("RS101", "사용 처리 할 수 없는 예약입니다."),
    RESERVATION_USAGE_FORBIDDEN("RS102", "사용할 수 없는 예약입니다."),
    RESERVATION_ALREADY_USED("RS103", "이미 사용 처리된 예약입니다."),
    RESERVATION_EXPIRED("RS104", "만료된 예약입니다."),
    RESERVATION_ALREADY_CANCELED("RS105", "이미 취소된 예약입니다."),

    DUPLICATED_REQUEST("RS200", "동일한 대기열 요청을 중복해서 등록할 수 없습니다."),
    NOT_FOUND("RS201", "대상을 찾을 수 없습니다."),
    UNAUTHORIZED_CANCELLATION("RS202", "대기열 요청을 취소할 권한이 없습니다."),

    ROOM_ALREADY_EXISTS("RS301", "방이 이미 존재합니다.");

    private final String code;
    private final String message;
}
