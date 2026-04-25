package com.seatliberator.seatliberator.reservation.application.booking.contract;

import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyCheckResult;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

public interface ReservationPolicyChecker {
    ReservationPolicyCheckResult check(String userId, SeatLocator locator, TimeRange range);
}
