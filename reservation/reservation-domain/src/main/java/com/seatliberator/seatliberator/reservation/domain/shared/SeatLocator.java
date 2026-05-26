package com.seatliberator.seatliberator.reservation.domain.shared;

public interface SeatLocator {
    String roomCode();

    String seatCode();

    default SeatLocatorKey key() {
        return SeatLocatorKey.from(this);
    }

    default boolean isSame(SeatLocator other) {
        return key().equals(other.key());
    }
}
