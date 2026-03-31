package com.seatliberator.seatliberator.reservation.vacancy.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertStatus;
import com.seatliberator.seatliberator.reservation.shared.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.shared.domain.TimeRange;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VacancyAlertRequestReader {
    boolean existsByUserIdAndLocatorAndRangeAndStatus(String userId, SeatLocator locator, TimeRange range, VacancyAlertStatus status);

    Optional<VacancyAlertRequest> findById(UUID id);

    List<VacancyAlertRequest> findByLocatorAndRange(SeatLocator locator, TimeRange range);

    List<VacancyAlertRequest> findByLocatorAndRangeAndStatus(SeatLocator locator, TimeRange range, VacancyAlertStatus status);
}
