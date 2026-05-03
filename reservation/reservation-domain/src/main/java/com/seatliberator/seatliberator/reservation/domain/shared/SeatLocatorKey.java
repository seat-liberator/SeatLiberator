package com.seatliberator.seatliberator.reservation.domain.shared;

public record SeatLocatorKey(String roomId, String seatId) implements Comparable<SeatLocatorKey> {
    public SeatLocatorKey {
        if (roomId.isBlank()) throw new IllegalArgumentException("roomId must not be blank.");
        if (seatId.isBlank()) throw new IllegalArgumentException("seatId must not be blank.");
    }

    public static SeatLocatorKey from(SeatLocator locator) {
        return new SeatLocatorKey(locator.roomId(), locator.seatId());
    }

    @Override
    public int compareTo(SeatLocatorKey other) {
        int roomCompare = roomId.compareTo(other.roomId);
        if (roomCompare != 0) return roomCompare;
        return seatId.compareTo(other.seatId);
    }
}
