package com.seatliberator.seatliberator.reservation.vacancy.application.port.in.entry;

import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;

public record VacancyAlertRequestEntry(
        String userId,
        SimpleSeatLocator locator,
        SimpleTimeRange range
) {
    public static VacancyAlertRequestEntry from(VacancyAlertRequest request) {
        return new VacancyAlertRequestEntry(
                request.getUserId(),
                SimpleSeatLocator.of(request.getLocator()),
                SimpleTimeRange.of(request.getRange())
        );
    }
}
