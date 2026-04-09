package com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command;

import java.util.UUID;

public record VacancyAlertRequestCancelCommand(
        String userId,
        UUID alertId
) {
}
