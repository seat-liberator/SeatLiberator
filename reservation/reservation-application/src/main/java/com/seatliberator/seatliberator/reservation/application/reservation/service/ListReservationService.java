package com.seatliberator.seatliberator.reservation.application.reservation.service;

import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationReadAuthorizer;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.ListReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.ListReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListReservationService implements ListReservationUseCase {
    private final ReservationReader reader;
    private final ReservationReadAuthorizer authorizer;
    private final ActorContextHolder actorContextHolder;

    @Override
    public List<ReservationResult> list(ListReservationQuery query) {
        var actor = actorContextHolder.getActor();

        authorizer.validate(actor);

        return reader.findByFilter(query.toFilter()).stream()
                .map(ReservationResult::from)
                .toList();
    }
}
