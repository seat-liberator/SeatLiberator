package com.seatliberator.seatliberator.reservation.application.booking.contract.command;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

public record ReservationCreatorCommand(
        String userId,
        SeatLocator locator,
        TimeRange range
) {
    public static ReservationCreatorCommand from(CreateReservationCommand command) {
        return new ReservationCreatorCommand(
                command.userId(),
                command.locator(),
                command.range()
        );
    }
}
