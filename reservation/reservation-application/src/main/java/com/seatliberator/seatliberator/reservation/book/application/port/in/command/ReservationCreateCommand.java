package com.seatliberator.seatliberator.reservation.book.application.port.in.command;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.time.Instant;

public record ReservationCreateCommand(
        String userId,
        String roomId,
        String seatId,
        Instant startTime,
        Instant endTime
) {
    public static ReservationCreateCommand of(String userId, SeatLocator locator, TimeRange range) {
        return new ReservationCreateCommand(
                userId,
                locator.roomId(),
                locator.seatId(),
                range.startAt(),
                range.endAt()
        );
    }
}
