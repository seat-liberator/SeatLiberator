package com.seatliberator.seatliberator.reservation.vacancy.application.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;

public record VacancyAlertRequestResult(
        String userId,
        SimpleSeatLocator locator,
        SimpleTimeRange range
) {
    public static VacancyAlertRequestResult from(VacancyAlertRequest request) {
        return new VacancyAlertRequestResult(
                request.getUserId(),
                SimpleSeatLocator.of(request.getLocator()),
                SimpleTimeRange.of(request.getRange())
        );
    }
}
