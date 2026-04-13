package com.seatliberator.seatliberator.reservation.vacancy.application.model;

import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;

import java.util.List;

public record VacancyAlertProcessingResult(
        List<VacancyAlertRequest> requestsToSave,
        List<VacancyAlertNotification> notifications
) {
    public VacancyAlertProcessingResult {
        requestsToSave = List.copyOf(requestsToSave);
        notifications = List.copyOf(notifications);
    }

    public boolean isEmpty() {
        return requestsToSave.isEmpty() && notifications.isEmpty();
    }
}
