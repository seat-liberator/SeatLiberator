package com.seatliberator.seatliberator.verification.application.service;

import com.seatliberator.seatliberator.reservation.application.port.in.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationLocator;
import com.seatliberator.seatliberator.reservation.application.port.in.entry.ReservationEntry;
import com.seatliberator.seatliberator.verification.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.verification.application.exception.ApplicationException;
import com.seatliberator.seatliberator.verification.application.policy.ReservationPolicyEngine;
import com.seatliberator.seatliberator.verification.application.port.in.ReservationPolicyReader;
import com.seatliberator.seatliberator.verification.application.port.in.command.Requester;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultReservationPolicyReader implements ReservationPolicyReader {
    private final ReservationReader reservationReader;
    private final ReservationPolicyEngine reservationPolicyEngine;

    @Override
    public ReservationEntry read(ReservationLocator reservationLocator, Requester requester) {
        var reservation = reservationReader.read(reservationLocator);

        if (!reservationPolicyEngine.canRead(reservation.reservationId(), requester)) {
            throw new ApplicationException(ApplicationErrorCode.RESERVATION_READ_FORBIDDEN);
        }

        return reservation;
    }
}

