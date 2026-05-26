package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Domain: Embeddable Seat Locator")
public class EmbeddableSeatLocatorTest implements SeatLocatorContractTest<EmbeddableSeatLocator> {
    @Override
    public EmbeddableSeatLocator create(String roomCode, String seatCode) {
        return EmbeddableSeatLocator.from(roomCode, seatCode);
    }

    public String getRoomCode() {
        return "room-1";
    }

    public String getSeatCode() {
        return "seat-1";
    }
}
