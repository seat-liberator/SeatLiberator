package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record SimpleSeatLocator(
        String roomCode,
        String seatCode
) implements SeatLocator {
    public SimpleSeatLocator {
        Preconditions.requireNonBlank(roomCode, "roomCode");
        Preconditions.requireNonBlank(seatCode, "seatCode");
    }

    public static SimpleSeatLocator of(String roomCode, String seatCode) {
        return new SimpleSeatLocator(roomCode, seatCode);
    }

    public static SimpleSeatLocator from(SeatLocator locator) {
        return new SimpleSeatLocator(locator.roomCode(), locator.seatCode());
    }
}
