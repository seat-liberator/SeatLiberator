package com.seatliberator.seatliberator.reservation.application.booking.port.in.command;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

public record CreateReservationCommand(
        String userId,
        SeatLocator locator,
        TimeRange range,
        Actor requester
) {
    public static CreateReservationCommand of(String userId, SeatLocator locator, TimeRange range, Actor requester) {
        return new CreateReservationCommand(
                userId,
                locator,
                range,
                requester
        );
    }
}
