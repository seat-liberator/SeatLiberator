package com.seatliberator.seatliberator.reservation.domain;

public interface SeatLocator {
    String roomId();

    String seatId();

    default boolean isSame(SeatLocator other) {
        return roomId().equals(other.roomId()) && seatId().equals(other.seatId());
    }
}
