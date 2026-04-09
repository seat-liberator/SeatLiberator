package com.seatliberator.seatliberator.reservation.domain;

public enum VacancyAlertRequestStatus {
    ACTIVE,
    CANCELLED,
    EXPIRED,
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

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
