package com.seatliberator.seatliberator.reservation.application.seat.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatStatus;

import java.time.Instant;
import java.util.UUID;

public record SeatResult(
        UUID seatId,
        String code,
        Instant createdAt,
        SeatStatus status,
        Instant lastActivatedAt,
        Instant lastInactivatedAt
) {
    public static SeatResult from(Seat seat) {
        return new SeatResult(
                seat.getId(),
                seat.getCode(),
                seat.getCreatedAt(),
                seat.getStatus(),
                seat.getLastActivatedAt(),
                seat.getLastInactivatedAt()
        );
    }
}