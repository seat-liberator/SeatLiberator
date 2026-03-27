package com.seatliberator.seatliberator.vacancy.application.port.in;

import com.seatliberator.seatliberator.vacancy.application.port.in.entry.VacancyAlertNotificationEntry;

import java.util.List;

public interface VacancyAlertNotificationReader {
    List<VacancyAlertNotificationEntry> readAllByUser(String userId, String actorId);
}
