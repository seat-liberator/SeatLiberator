package com.seatliberator.seatliberator.reservation.application.seat.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public record SeatTimeSlotResult(
        UUID id,
        LocalTime startAt,
        Duration duration,
        SeatTimeSlotStatus slotStatus,
        Instant createdAt,
        Instant lastActivatedAt,
        Instant lastInactivatedAt
) {
    public static SeatTimeSlotResult from(SeatTimeSlot seatTimeSlot) {
        return new SeatTimeSlotResult(
                seatTimeSlot.getId(),
                seatTimeSlot.getSlotRange().startAt(),
                seatTimeSlot.getSlotRange().duration(),
                seatTimeSlot.getSlotStatus(),
                seatTimeSlot.getCreatedAt(),
                seatTimeSlot.getLastActivatedAt(),
                seatTimeSlot.getLastInactivatedAt()
        );
    }
}
