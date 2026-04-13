package com.seatliberator.seatliberator.reservation.verification.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.contract.query.ReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.verification.application.port.in.command.Requester;

public interface FindReservationByPolicyUseCase {
    ReservationResult read(ReservationLocator reservationLocator, Requester requester);
}
