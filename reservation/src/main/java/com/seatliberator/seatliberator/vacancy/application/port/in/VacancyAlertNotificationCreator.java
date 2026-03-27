package com.seatliberator.seatliberator.vacancy.application.port.in;

import com.seatliberator.seatliberator.vacancy.application.port.in.command.VacancyAlertNotificationCreateCommand;
import com.seatliberator.seatliberator.vacancy.application.port.in.entry.VacancyAlertNotificationEntry;

import java.util.List;

public interface VacancyAlertNotificationCreator {
    List<VacancyAlertNotificationEntry> createAll(List<VacancyAlertNotificationCreateCommand> commands);
}
