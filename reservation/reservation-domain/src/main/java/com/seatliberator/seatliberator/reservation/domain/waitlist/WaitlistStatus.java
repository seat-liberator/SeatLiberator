package com.seatliberator.seatliberator.reservation.domain.waitlist;

public enum WaitlistStatus {
    ACTIVE,
    CANCELLED,
    EXPIRED,
    FAILED,
    COMPLETED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }

    public boolean isExpired() {
        return this == EXPIRED;
    }

    public boolean isFailed() {
        return this == FAILED;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
