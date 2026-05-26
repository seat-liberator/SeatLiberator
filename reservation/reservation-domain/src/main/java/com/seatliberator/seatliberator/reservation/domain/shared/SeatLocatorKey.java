package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record SeatLocatorKey(String roomCode, String seatCode) implements Comparable<SeatLocatorKey> {
    public SeatLocatorKey {
        Preconditions.requireNonBlank(roomCode, "roomCode");
        Preconditions.requireNonBlank(seatCode, "seatCode");
    }

    public static SeatLocatorKey from(SeatLocator locator) {
        return new SeatLocatorKey(locator.roomCode(), locator.seatCode());
    }

    @Override
    public int compareTo(SeatLocatorKey other) {
        int roomCompare = roomCode.compareTo(other.roomCode);
        if (roomCompare != 0) return roomCompare;
        return seatCode.compareTo(other.seatCode);
    }
}
