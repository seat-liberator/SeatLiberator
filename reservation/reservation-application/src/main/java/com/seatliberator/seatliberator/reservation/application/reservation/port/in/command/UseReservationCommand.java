package com.seatliberator.seatliberator.reservation.application.reservation.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UseReservationCommand(UUID reservationId) {
    public UseReservationCommand {
        Preconditions.requireNonNull(reservationId, "reservationId");
    }

    public static UseReservationCommand of(UUID reservationId) {
        return new UseReservationCommand(reservationId);
    }
}
