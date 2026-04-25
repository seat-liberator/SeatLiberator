package com.seatliberator.seatliberator.reservation.application.booking.contract.result;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReservationRejectReason implements RejectReason {
    USER_BLOCKED("사용이 제한된 사용자"),
    SEAT_ALREADY_TAKEN("이미 예약된 좌석"),
    POLICY_VIOLATION("예약 정책 위반");

    private final String message;

    @Override
    public String message() {
        return message;
    }
}
