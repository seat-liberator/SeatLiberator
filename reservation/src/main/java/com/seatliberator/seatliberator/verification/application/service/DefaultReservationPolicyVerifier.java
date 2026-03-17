package com.seatliberator.seatliberator.verification.application.service;

import com.seatliberator.seatliberator.reservation.application.port.in.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.port.in.ReservationUsageMarker;
import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationLocator;
import com.seatliberator.seatliberator.reservation.application.port.in.entry.ReservationEntry;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.verification.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.verification.application.exception.ApplicationException;
import com.seatliberator.seatliberator.verification.application.policy.ReservationPolicyEngine;
import com.seatliberator.seatliberator.verification.application.port.in.ReservationVerifier;
import com.seatliberator.seatliberator.verification.application.port.in.command.Requester;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultReservationPolicyVerifier implements ReservationVerifier {
    private final ReservationReader reservationReader;
    private final ReservationUsageMarker reservationUsageMarker;
    private final ReservationPolicyEngine reservationPolicyEngine;

    @Override
    @Transactional
    public ReservationEntry verify(ReservationLocator reservationLocator, Requester requester) {
        var reservation = reservationReader.read(reservationLocator);

        if (!reservationPolicyEngine.canVerify(reservation.reservationId(), requester)) {
            throw new ApplicationException(ApplicationErrorCode.RESERVATION_VERIFY_FORBIDDEN);
        }

        var transition = reservationUsageMarker.markUsed(reservation.reservationId());

        if (!transition.success()) {
            throw new ApplicationException(resolveErrorCode(transition.status()));
        }

        return reservation;
    }

    private ApplicationErrorCode resolveErrorCode(ReservationStatus status) {
        return switch (status) {
            case EXPIRED -> ApplicationErrorCode.RESERVATION_EXPIRED;
            case USED -> ApplicationErrorCode.RESERVATION_ALREADY_USED;
            case RESERVED -> ApplicationErrorCode.RESERVATION_USAGE_FORBIDDEN;
        };
    }
}
