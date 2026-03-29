package com.seatliberator.seatliberator.reservation.verification.application.service;

import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationUsageMarker;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationEntry;
import com.seatliberator.seatliberator.reservation.book.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.verification.application.exception.VerificationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.verification.application.exception.VerificationApplicationException;
import com.seatliberator.seatliberator.reservation.verification.application.policy.ReservationPolicyEngine;
import com.seatliberator.seatliberator.reservation.verification.application.port.in.ReservationVerifier;
import com.seatliberator.seatliberator.reservation.verification.application.port.in.command.Requester;
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
            throw new VerificationApplicationException(VerificationApplicationErrorCode.RESERVATION_VERIFY_FORBIDDEN);
        }

        var transition = reservationUsageMarker.markUsed(reservation.reservationId());

        if (!transition.success()) {
            throw new VerificationApplicationException(resolveErrorCode(transition.status()));
        }

        return reservation;
    }

    private VerificationApplicationErrorCode resolveErrorCode(ReservationStatus status) {
        return switch (status) {
            case EXPIRED -> VerificationApplicationErrorCode.RESERVATION_EXPIRED;
            case USED -> VerificationApplicationErrorCode.RESERVATION_ALREADY_USED;
            case RESERVED -> VerificationApplicationErrorCode.RESERVATION_USAGE_FORBIDDEN;
        };
    }
}
