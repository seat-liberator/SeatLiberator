package com.seatliberator.seatliberator.reservation.book.application.port.in;

public interface ReservationOwnershipChecker {
    boolean hasOwnership(Long reservationId, String userId);
}
