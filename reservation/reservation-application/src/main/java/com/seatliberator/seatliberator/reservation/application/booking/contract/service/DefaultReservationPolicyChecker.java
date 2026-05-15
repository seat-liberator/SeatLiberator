package com.seatliberator.seatliberator.reservation.application.booking.contract.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationPolicyChecker;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyCheckResult;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationRejectReason;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationSeatLookupCriteria;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultReservationPolicyChecker implements ReservationPolicyChecker {
    private final ReservationReader reader;

    @Override
    public ReservationPolicyCheckResult check(String userId, SeatLocator locator, InstantRange range) {
        var criteria = ReservationSeatLookupCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
        var exists = reader.existsOne(criteria);

        if (exists) return ReservationPolicyCheckResult.reject(ReservationRejectReason.SEAT_ALREADY_TAKEN);

        return ReservationPolicyCheckResult.accept();
    }
}
