package com.seatliberator.seatliberator.reservation.application.booking.port.in.command;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.time.Instant;

public record CreateReservationCommand(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) {
    public static CreateReservationCommand of(String userId, SeatLocator locator, TimeRange range) {
        return new CreateReservationCommand(
                userId,
                locator.roomId(),
                locator.seatId(),
                range.startAt(),
                range.endAt()
        );
    }
}
