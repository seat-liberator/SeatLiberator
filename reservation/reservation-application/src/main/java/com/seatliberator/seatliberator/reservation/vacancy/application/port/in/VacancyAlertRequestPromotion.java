package com.seatliberator.seatliberator.reservation.vacancy.application.port.in;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.entry.VacancyAlertRequestPromotionResult;

public interface VacancyAlertRequestPromotion {
    VacancyAlertRequestPromotionResult promote(String userId, SeatLocator locator, TimeRange range);
}
