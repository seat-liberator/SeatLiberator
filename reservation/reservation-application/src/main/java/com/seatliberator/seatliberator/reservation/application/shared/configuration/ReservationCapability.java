package com.seatliberator.seatliberator.reservation.application.shared.configuration;

import com.seatliberator.seatliberator.identity.core.role.Capability;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReservationCapability implements Capability {
    ROOM_LIST("room.list", "방 목록 조회"),
    ROOM_READ("room.read", "방 조회"),
    ROOM_MANAGE("room.manage", "방 관리"),

    SEAT_LIST("seat.list", "좌석 목록 조회"),
    SEAT_READ("seat.read", "좌석 조회"),
    SEAT_MANAGE("seat.manage", "좌석 관리"),

    BOOKING_CREATE("booking.create", "예약 생성"),
    OWNED_BOOKING_CANCEL("owned.booking.cancel", "예약 취소"),
    OWNED_BOOKING_UPDATE("owned.booking.update", "예약 수정"),
    BOOKING_MANAGE("booking.manage", "예약 관리"),

    WAITLIST_CREATE("waitlist.create", "대기열 생성"),
    WAITLIST_CANCEL("waitlist.cancel", "대기열 취소"),
    WAITLIST_MANAGE("waitlist.manage", "대기열 관리");

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
