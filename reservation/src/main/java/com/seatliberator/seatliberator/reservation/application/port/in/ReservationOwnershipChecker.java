package com.seatliberator.seatliberator.reservation.application.port.in;

public interface ReservationOwnershipChecker {
    boolean hasOwnership(Long reservationId, String userId);
}
