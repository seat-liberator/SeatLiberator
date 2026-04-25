package com.seatliberator.seatliberator.reservation.application.verification.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationUsageMarker;
import com.seatliberator.seatliberator.reservation.application.booking.contract.query.ReservationLocator;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.verification.in.FindReservationByPolicyUseCase;
import com.seatliberator.seatliberator.reservation.application.verification.in.VerifyReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.verification.in.command.Requester;
import com.seatliberator.seatliberator.reservation.application.verification.policy.ReservationPolicyEngine;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerificationService implements
        FindReservationByPolicyUseCase,
        VerifyReservationUseCase {
    private final FindReservationUseCase findReservationUseCase;
    private final ReservationPolicyEngine reservationPolicyEngine;
    private final ReservationUsageMarker reservationUsageMarker;

    @Override
    public ReservationResult read(ReservationLocator reservationLocator, Requester requester) {
        var reservation = findReservationUseCase.find(reservationLocator);

        if (!reservationPolicyEngine.canRead(reservation.reservationId(), requester)) {
            throw new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_READ_FORBIDDEN);
        }

        return reservation;
    }

    @Override
    @Transactional
    public ReservationResult verify(ReservationLocator reservationLocator, Requester requester) {
        var reservation = findReservationUseCase.find(reservationLocator);

        if (!reservationPolicyEngine.canVerify(reservation.reservationId(), requester)) {
            throw new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_VERIFY_FORBIDDEN);
        }

        var transition = reservationUsageMarker.markUsed(reservation.reservationId());

        if (!transition.success()) {
            throw new ReservationApplicationException(resolveErrorCode(transition.status()));
        }

        return reservation;
    }

    private ReservationApplicationErrorCode resolveErrorCode(ReservationStatus status) {
        return switch (status) {
            case EXPIRED -> ReservationApplicationErrorCode.RESERVATION_EXPIRED;
            case USED -> ReservationApplicationErrorCode.RESERVATION_ALREADY_USED;
            case RESERVED -> ReservationApplicationErrorCode.RESERVATION_USAGE_FORBIDDEN;
            case CANCELED -> ReservationApplicationErrorCode.RESERVATION_ALREADY_CANCELED;
        };
    }
}
