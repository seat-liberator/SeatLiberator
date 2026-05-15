package com.seatliberator.seatliberator.reservation.application.booking.contract.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;

public record ReservationCreatePolicyCommand(
        String userId,
        SeatLocator locator,
        InstantRange range
) {
    public ReservationCreatePolicyCommand {
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonNull(locator, "locator");
        Preconditions.requireNonNull(range, "range");
    }

    public static ReservationCreatePolicyCommand of(String userId, SeatLocator locator, InstantRange range) {
        return new ReservationCreatePolicyCommand(userId, locator, range);
    }

    public static ReservationCreatePolicyCommand from(CreateReservationCommand command) {
        return of(command.userId(), command.locator(), command.range());
    }
}
