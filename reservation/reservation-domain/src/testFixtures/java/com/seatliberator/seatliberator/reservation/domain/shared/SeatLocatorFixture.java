package com.seatliberator.seatliberator.reservation.domain.shared;

public class SeatLocatorFixture {
    private static final String ROOM_CODE = "room-1";
    private static final String SEAT_CODE = "seat-1";

    public static SeatLocator get() {
        return get(ROOM_CODE, SEAT_CODE);
    }

    public static SeatLocator get(String roomCode, String seatCode) {
        return SimpleSeatLocator.of(roomCode, seatCode);
    }
}
