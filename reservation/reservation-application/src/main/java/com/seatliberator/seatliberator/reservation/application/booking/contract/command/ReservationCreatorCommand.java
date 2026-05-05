package com.seatliberator.seatliberator.reservation.application.booking.contract.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.shared.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;

public record ReservationCreatorCommand(
        String userId,
        SeatLocator locator,
        InstantRange range
) {
    public ReservationCreatorCommand {
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonNull(locator, "locator");
        Preconditions.requireNonNull(range, "range");
    }

    public static ReservationCreatorCommand of(String userId, SeatLocator locator, InstantRange range) {
        return new ReservationCreatorCommand(userId, locator, range);
    }

    public static ReservationCreatorCommand from(CreateReservationCommand command) {
        return of(command.userId(), command.locator(), command.range());
    }
}
