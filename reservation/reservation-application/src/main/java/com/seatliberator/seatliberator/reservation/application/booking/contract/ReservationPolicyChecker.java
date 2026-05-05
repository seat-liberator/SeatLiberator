package com.seatliberator.seatliberator.reservation.application.booking.contract;

import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyCheckResult;
import com.seatliberator.seatliberator.reservation.domain.shared.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;

public interface ReservationPolicyChecker {
    ReservationPolicyCheckResult check(String userId, SeatLocator locator, InstantRange range);
}
