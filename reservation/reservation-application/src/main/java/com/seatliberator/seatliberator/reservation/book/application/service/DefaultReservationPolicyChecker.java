package com.seatliberator.seatliberator.reservation.book.application.service;

import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationExistenceChecker;
import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationPolicyChecker;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationPolicyCheckResult;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationRejectReason;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultReservationPolicyChecker implements ReservationPolicyChecker {
    private final ReservationExistenceChecker existenceChecker;

    @Override
    public ReservationPolicyCheckResult check(String userId, SeatLocator locator, TimeRange range) {
        var exists = existenceChecker.isExistsByLocatorAndRangeAndStatus(locator, range, ReservationStatus.RESERVED);

        if (exists) return ReservationPolicyCheckResult.reject(ReservationRejectReason.SEAT_ALREADY_TAKEN);

        return ReservationPolicyCheckResult.accept();
    }
}
