package com.seatliberator.seatliberator.reservation.application.usage.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.usage.port.in.UseReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.usage.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.application.usage.port.in.result.UseReservationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationUsageService implements
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
