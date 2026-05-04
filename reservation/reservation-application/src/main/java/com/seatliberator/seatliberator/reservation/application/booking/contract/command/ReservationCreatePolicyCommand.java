package com.seatliberator.seatliberator.reservation.application.booking.contract.command;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

public record ReservationCreatePolicyCommand(
        String userId,
        SeatLocator locator,
        TimeRange range,
        Actor request
) {
    public static ReservationCreatePolicyCommand from(CreateReservationCommand command, Actor requester) {
        var locator = SimpleSeatLocator.of(command.roomId(), command.seatId());
        var range = SimpleTimeRange.of(command.startTime(), command.endTime());
        return new ReservationCreatePolicyCommand(
                command.userId(),
                locator,
                range,
                requester
        );
    }
}
