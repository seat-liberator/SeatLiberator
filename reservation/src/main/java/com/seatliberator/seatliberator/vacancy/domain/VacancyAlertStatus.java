package com.seatliberator.seatliberator.vacancy.domain;

public enum VacancyAlertStatus {
    ACTIVE,
    CANCELLED,
    EXPIRED,
    FULFILLED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
