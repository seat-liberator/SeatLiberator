package com.seatliberator.seatliberator.reservation.application.reservation.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationReadAuthorizer;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.FindReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.FindReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindReservationService implements FindReservationUseCase {
    private final ReservationReader reader;
    private final ReservationReadAuthorizer authorizer;
    private final ReservationOwnershipPolicy ownershipPolicy;
    private final ActorContextHolder actorContextHolder;

    @Override
    public ReservationResult find(FindReservationQuery query) {
        var actor = actorContextHolder.getActor();

        authorizer.validate(actor);

        var reservation = reader.findById(query.reservationId())
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));

        ownershipPolicy.validate(reservation, actor);

        return ReservationResult.from(reservation);
    }
}
