package com.seatliberator.seatliberator.reservation.application.booking.contract;

public interface ReservationOwnershipChecker {
    boolean hasOwnership(Long reservationId, String userId);
}
