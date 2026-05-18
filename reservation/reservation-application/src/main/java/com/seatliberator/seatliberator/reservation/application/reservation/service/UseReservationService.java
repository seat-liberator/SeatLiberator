package com.seatliberator.seatliberator.reservation.application.reservation.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.UseReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class UseReservationService implements
        UseReservationUseCase {
    private final ReservationReader reader;
    private final ReservationStore store;
    private final ReservationOwnershipPolicy ownershipPolicy;

    private final ActorContextHolder actorContextHolder;
    private final Clock clock;

    @Override
    public ReservationResult use(UseReservationCommand command) {
        var actor = actorContextHolder.getActor();

        var now = clock.instant();
        var reservationId = command.reservationId();

        var reservation = reader.findById(reservationId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));

        ownershipPolicy.validate(reservation, actor);

        reservation.use(now);
        var saved = store.save(reservation);
        return ReservationResult.from(saved);
    }
}
