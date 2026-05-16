package com.seatliberator.seatliberator.reservation.domain.reservation;

public enum ReservationStatus {
    RESERVED,
    USED,
    CANCELLED,
    EXPIRED;

    public boolean isReserved() {
        return this == RESERVED;
    }

    public boolean isUsed() {
        return this == USED;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }

    public boolean isExpired() {
        return this == EXPIRED;
    }
}
