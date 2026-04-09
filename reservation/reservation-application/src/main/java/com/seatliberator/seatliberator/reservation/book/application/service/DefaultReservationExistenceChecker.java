package com.seatliberator.seatliberator.reservation.book.application.service;

import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationExistenceChecker;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
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
}
