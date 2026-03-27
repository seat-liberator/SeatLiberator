package com.seatliberator.seatliberator.vacancy.application.service;

import com.seatliberator.seatliberator.vacancy.application.port.in.VacancyAlertNotificationCreator;
import com.seatliberator.seatliberator.vacancy.application.port.in.command.VacancyAlertNotificationCreateCommand;
import com.seatliberator.seatliberator.vacancy.application.port.in.entry.VacancyAlertNotificationEntry;
import com.seatliberator.seatliberator.vacancy.application.port.out.VacancyAlertNotificationStore;
import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultVacancyAlertNotificationCreator implements VacancyAlertNotificationCreator {
    private final VacancyAlertNotificationStore notificationStore;

    @Override
    public List<VacancyAlertNotificationEntry> createAll(List<VacancyAlertNotificationCreateCommand> commands) {
        if (commands.isEmpty()) {
            return List.of();
        }

        var notifications = commands.stream()
                .map(command -> VacancyAlertNotification.of(
                        command.vacancyAlertRequestId(),
                        command.userId(),
                        command.roomId(),
                        command.seatId(),
                        command.targetStartTime(),
                        command.targetEndTime(),
                        command.notifiedAt()
                ))
                .toList();

        return notificationStore.saveAll(notifications).stream()
                .map(VacancyAlertNotificationEntry::of)
                .toList();
    }
}
