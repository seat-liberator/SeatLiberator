package com.seatliberator.seatliberator.reservation.vacancy.domain;

public enum VacancyAlertStatus {
    ACTIVE,
    CANCELLED,
    EXPIRED,
    FULFILLED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
