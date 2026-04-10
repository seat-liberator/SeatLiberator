package com.seatliberator.seatliberator.reservation.shared.infrastructure.security;

import com.seatliberator.seatliberator.identity.client.role.Capability;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReservationCapability implements Capability {
    SEAT_LIST("seat.list", "자리 목록 조회"),
    SEAT_READ("seat.read", "자리 조회"),
    SEAT_MANAGE("seat.manage", "자리 관리"),

    BOOKING_CREATE("booking.create", "예약 생성"),
    OWNED_BOOKING_CANCEL("owned.booking.cancel", "예약 취소"),
    OWNED_BOOKING_UPDATE("owned.booking.update", "예약 수정"),
    BOOKING_MANAGE("booking.manage", "예약 관리");

    private final String scope;
    private final String description;

    @Override
    public String scope() {
        return scope;
    }

    @Override
    public String description() {
        return description;
    }
}
