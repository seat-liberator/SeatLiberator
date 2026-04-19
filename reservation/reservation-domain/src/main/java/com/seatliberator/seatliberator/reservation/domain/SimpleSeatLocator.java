package com.seatliberator.seatliberator.reservation.domain;

public record SimpleSeatLocator(
        String roomId,
        String seatId
) implements SeatLocator {
    public SimpleSeatLocator {
        if (roomId.isBlank()) {
            throw new IllegalArgumentException("roomId must not be null or blank.");
        }
        if (seatId.isBlank()) {
            throw new IllegalArgumentException("seatId must not be null or blank.");
        }
    }

    public static SimpleSeatLocator of(String roomId, String seatId) {
        return new SimpleSeatLocator(roomId, seatId);
    }

    public static SimpleSeatLocator from(SeatLocator locator) {
        return new SimpleSeatLocator(locator.roomId(), locator.seatId());
    }
}
