package com.seatliberator.seatliberator.reservation.book.application.contract;

import com.seatliberator.seatliberator.reservation.book.application.contract.result.ReservationPolicyCheckResult;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

public interface ReservationPolicyChecker {
    ReservationPolicyCheckResult check(String userId, SeatLocator locator, TimeRange range);
}
