package com.seatliberator.seatliberator.reservation.application.booking.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record CancelBookingCommand(UUID reservationId) {
    public CancelBookingCommand {
        Preconditions.requireNonNull(reservationId, "reservationId");
    }

    public static CancelBookingCommand of(UUID reservationId) {
        return new CancelBookingCommand(reservationId);
    }
}
