package com.seatliberator.seatliberator.vacancy.application.port.in.command;

import java.util.UUID;

public record VacancyAlertCancelCommand(
        String userId,
        UUID alertId
) {
}
