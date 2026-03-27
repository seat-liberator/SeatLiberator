package com.seatliberator.seatliberator.vacancy.application.port.out;

import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertNotification;

import java.util.List;

public interface VacancyAlertNotificationStore {
    List<VacancyAlertNotification> saveAll(List<VacancyAlertNotification> notifications);
}
