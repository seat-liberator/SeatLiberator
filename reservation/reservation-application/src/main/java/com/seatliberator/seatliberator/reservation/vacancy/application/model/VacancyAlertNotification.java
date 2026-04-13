package com.seatliberator.seatliberator.reservation.vacancy.application.model;

import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;

public record VacancyAlertNotification(
        VacancyAlertRequest request,
        String level,
        String title
) {
    public String userId() {
        return request.getUserId();
    }
}
