package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Domain: Simple Seat Locator")
public class SimpleSeatLocatorTest implements SeatLocatorContractTest<SimpleSeatLocator> {
    @Override
    public SimpleSeatLocator create(String roomCode, String seatCode) {
        return SimpleSeatLocator.of(roomCode, seatCode);
    }

    @Override
    public String getRoomCode() {
        return "room-1";
    }

    @Override
    public String getSeatCode() {
        return "seat-1";
    }
}
