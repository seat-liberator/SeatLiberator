package com.seatliberator.seatliberator.reservation.application.booking.port.in.command;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.reservation.domain.shared.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;

public record CreateReservationCommand(
        String userId,
        SeatLocator locator,
        InstantRange range,
        Actor requester
) {
    public static CreateReservationCommand of(String userId, SeatLocator locator, InstantRange range, Actor requester) {
        return new CreateReservationCommand(
                userId,
                locator,
                range,
                requester
        );
    }
}
