package com.seatliberator.seatliberator.reservation.application.booking.contract;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;

public interface ReservationOwnershipPolicy {
    ReservationPolicyResult evaluate(Reservation reservation, Actor requester);
}
