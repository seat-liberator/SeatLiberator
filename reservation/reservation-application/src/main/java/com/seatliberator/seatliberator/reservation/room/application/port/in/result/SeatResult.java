package com.seatliberator.seatliberator.reservation.room.application.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.SeatStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;

import java.time.Instant;

public record SeatResult(
        String seatId,
        Instant createdAt,
        SeatStatus status,
        Instant lastActivatedAt,
        Instant lastInactivatedAt
) {
    public static SeatResult from(Seat seat) {
        return new SeatResult(
                seat.getSeatId(),
                seat.getCreatedAt(),
                seat.getStatus(),
                seat.getLastActivatedAt(),
                seat.getLastInactivatedAt()
        );
    }
}