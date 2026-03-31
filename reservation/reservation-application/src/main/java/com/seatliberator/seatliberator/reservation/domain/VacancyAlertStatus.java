package com.seatliberator.seatliberator.reservation.domain;

public enum VacancyAlertStatus {
    ACTIVE,
    CANCELLED,
    EXPIRED,
    FULFILLED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
