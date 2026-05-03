package com.seatliberator.seatliberator.reservation.domain.shared;

public class SeatLocatorFixture {
    public static final String INITIAL_ROOM_ID = "room-1";
    public static final String INITIAL_SEAT_ID = "seat-1";

    public static SeatLocator createLocator() {
        return createLocator(INITIAL_ROOM_ID, INITIAL_SEAT_ID);
    }

    public static SeatLocator createLocator(String roomId, String seatId) {
        return SimpleSeatLocator.of(roomId, seatId);
    }
}
