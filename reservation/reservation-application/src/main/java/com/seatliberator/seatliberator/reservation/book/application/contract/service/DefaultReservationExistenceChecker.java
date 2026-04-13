package com.seatliberator.seatliberator.reservation.book.application.contract.service;

import com.seatliberator.seatliberator.reservation.book.application.contract.ReservationExistenceChecker;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultReservationExistenceChecker implements ReservationExistenceChecker {
    private final ReservationStore store;

    @Override
    public boolean isExistsByLocatorAndRange(SeatLocator locator, TimeRange range) {
        return store.existsByLocatorAndRange(locator, range);
    }

    @Override
    public boolean isExistsByLocatorAndRangeAndStatus(SeatLocator locator, TimeRange range, ReservationStatus status) {
        return store.existsByLocatorAndRangeAndStatus(locator, range, status);
    }
}
