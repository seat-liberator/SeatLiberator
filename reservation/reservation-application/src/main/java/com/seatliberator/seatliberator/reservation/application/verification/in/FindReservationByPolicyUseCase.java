package com.seatliberator.seatliberator.reservation.application.verification.in;

import com.seatliberator.seatliberator.reservation.application.booking.contract.query.ReservationLocator;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.verification.in.command.Requester;

public interface FindReservationByPolicyUseCase {
    ReservationResult read(ReservationLocator reservationLocator, Requester requester);
}
