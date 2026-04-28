package com.seatliberator.seatliberator.reservation.application.verification.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.verification.port.in.UseReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.verification.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.application.verification.port.in.result.UseReservationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class VerificationService implements
        UseReservationUseCase {
    private final ReservationReader reader;
    private final ReservationOwnershipPolicy ownershipPolicy;

    private final Clock clock;

    @Override
    public UseReservationResult use(UseReservationCommand command) {
        var now = clock.instant();
        var requester = command.requestedUser();
        var reservationId = command.reservationId();

        var reservation = reader.findById(reservationId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));

        var ownership = ownershipPolicy.evaluate(reservation, requester);

        if (ownership.accepted()) {
            reservation.use(now);
            return UseReservationResult.accept(now);
        } else {
            return UseReservationResult.reject(ownership.reason().message(), now);
        }
    }
}
