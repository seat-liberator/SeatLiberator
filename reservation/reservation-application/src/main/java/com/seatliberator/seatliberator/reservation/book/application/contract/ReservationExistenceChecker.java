package com.seatliberator.seatliberator.reservation.book.application.contract;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

public interface ReservationExistenceChecker {
    boolean isExistsByLocatorAndRange(SeatLocator locator, TimeRange range);

    boolean isExistsByLocatorAndRangeAndStatus(SeatLocator locator, TimeRange range, ReservationStatus status);
}
