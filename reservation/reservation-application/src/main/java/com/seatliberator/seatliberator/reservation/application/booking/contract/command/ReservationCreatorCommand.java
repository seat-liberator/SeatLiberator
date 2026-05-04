package com.seatliberator.seatliberator.reservation.application.booking.contract.command;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

public record ReservationCreatorCommand(
        String userId,
        SeatLocator locator,
        TimeRange range
) {
    public static ReservationCreatorCommand from(CreateReservationCommand command) {
        var locator = SimpleSeatLocator.of(command.roomId(), command.seatId());
        var range = SimpleTimeRange.of(command.startTime(), command.endTime());

        return new ReservationCreatorCommand(
                command.userId(),
                locator,
                range
        );
    }
}
