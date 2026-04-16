package com.seatliberator.seatliberator.reservation.domain;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Domain: Simple Seat Locator")
public class SimpleSeatLocatorTest implements SeatLocatorContractTest<SimpleSeatLocator> {
    @Override
    public SimpleSeatLocator create(String roomId, String seatId) {
        return SimpleSeatLocator.from(roomId, seatId);
    }

    @Override
    public String getRoomId() {
        return "room-1";
    }

    @Override
    public String getSeatId() {
        return "seat-1";
    }
}
