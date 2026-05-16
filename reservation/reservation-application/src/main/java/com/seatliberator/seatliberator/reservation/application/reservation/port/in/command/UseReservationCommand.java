package com.seatliberator.seatliberator.reservation.application.reservation.port.in.command;

import com.seatliberator.seatliberator.identity.core.actor.Actor;

import java.util.UUID;

public record UseReservationCommand(
        UUID reservationId,
        Actor requestedUser
) {
}
