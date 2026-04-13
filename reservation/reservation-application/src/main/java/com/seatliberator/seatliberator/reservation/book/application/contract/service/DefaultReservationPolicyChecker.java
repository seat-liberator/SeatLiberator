package com.seatliberator.seatliberator.reservation.book.application.contract.service;

import com.seatliberator.seatliberator.reservation.book.application.contract.ReservationExistenceChecker;
import com.seatliberator.seatliberator.reservation.book.application.contract.result.ReservationPolicyCheckResult;
import com.seatliberator.seatliberator.reservation.book.application.contract.ReservationPolicyChecker;
import com.seatliberator.seatliberator.reservation.book.application.contract.result.ReservationRejectReason;
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
