package com.seatliberator.seatliberator.reservation.application.verification.port.in.command;

import com.seatliberator.seatliberator.identity.core.actor.Actor;

public record UseReservationCommand(
        Long reservationId,
        Actor requestedUser
) {
}
