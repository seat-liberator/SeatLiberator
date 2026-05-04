package com.seatliberator.seatliberator.reservation.application.booking.contract.command;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

public record ReservationCreatePolicyCommand(
        String userId,
        SeatLocator locator,
        TimeRange range
) {
    public static ReservationCreatePolicyCommand from(CreateReservationCommand command) {
        return new ReservationCreatePolicyCommand(
                command.userId(),
                command.locator(),
                command.range()
        );
    }
}
