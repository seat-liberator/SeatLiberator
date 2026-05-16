package com.seatliberator.seatliberator.reservation.application.reservation.contract;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
@RequiredArgsConstructor
public class ReservationCreator {
    private final CreateAuthorizer authorizer;
    private final ReservationStore store;
    private final Clock clock;

    public Reservation create(String userId) {
        var now = clock.instant();
        var reservation = Reservation.of(userId, now);
        return store.save(reservation);
    }

    public Reservation createAuthorized(String userId, Actor actor) {
        authorizer.ensureAuthorized(actor);
        return create(userId);
    }
}
