package com.seatliberator.seatliberator.reservation.domain;

public interface SeatLocator {
    String roomId();

    String seatId();

    default SeatLocatorKey key() {
        return SeatLocatorKey.from(this);
    }

    default boolean isSame(SeatLocator other) {
        return key().equals(other.key());
    }
}
