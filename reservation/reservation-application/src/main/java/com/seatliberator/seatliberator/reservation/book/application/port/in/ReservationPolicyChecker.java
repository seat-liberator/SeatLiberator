package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationPolicyCheckResult;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

public interface ReservationPolicyChecker {
    ReservationPolicyCheckResult check(String userId, SeatLocator locator, TimeRange range);
}
