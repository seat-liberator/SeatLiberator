package com.seatliberator.seatliberator.reservation.application.seat.port.out.criteria;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record SeatLookupCriteria(
        UUID roomId,
        String seatCode
) {
    public SeatLookupCriteria {
        Preconditions.requireNonNull(roomId, "roomId");
        Preconditions.requireNonBlank(seatCode, "seatCode");
    }

    public static SeatLookupCriteria of(UUID roomId, String seatCode) {
        return new SeatLookupCriteria(roomId, seatCode);
    }
}
