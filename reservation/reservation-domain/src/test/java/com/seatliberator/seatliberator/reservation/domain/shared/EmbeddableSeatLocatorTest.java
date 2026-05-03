package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Domain: Embeddable Seat Locator")
public class EmbeddableSeatLocatorTest implements SeatLocatorContractTest<EmbeddableSeatLocator> {
    @Override
    public EmbeddableSeatLocator create(String roomId, String seatId) {
        return EmbeddableSeatLocator.from(roomId, seatId);
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
