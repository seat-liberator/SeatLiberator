package com.seatliberator.seatliberator.reservation.book.application.contract;

public interface ReservationOwnershipChecker {
    boolean hasOwnership(Long reservationId, String userId);
}
