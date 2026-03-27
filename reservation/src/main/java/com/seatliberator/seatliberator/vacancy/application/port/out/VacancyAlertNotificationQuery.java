package com.seatliberator.seatliberator.vacancy.application.port.out;

import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertNotification;

import java.util.List;

public interface VacancyAlertNotificationQuery {
    List<VacancyAlertNotification> findAllByUserIdOrderByNotifiedAtDesc(String userId);
}
