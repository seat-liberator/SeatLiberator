package com.seatliberator.seatliberator.vacancy.application.service;

import com.seatliberator.seatliberator.vacancy.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.vacancy.application.exception.ApplicationException;
import com.seatliberator.seatliberator.vacancy.application.port.in.VacancyAlertNotificationReader;
import com.seatliberator.seatliberator.vacancy.application.port.in.entry.VacancyAlertNotificationEntry;
import com.seatliberator.seatliberator.vacancy.application.port.out.VacancyAlertNotificationQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultVacancyAlertNotificationReader implements VacancyAlertNotificationReader {
    private final VacancyAlertNotificationQuery notificationQuery;

    @Override
    public List<VacancyAlertNotificationEntry> readAllByUser(String userId, String actorId) {
        if (!userId.equals(actorId)) {
            throw new ApplicationException(ApplicationErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        return notificationQuery.findAllByUserIdOrderByNotifiedAtDesc(userId).stream()
                .map(VacancyAlertNotificationEntry::of)
                .toList();
    }
}
