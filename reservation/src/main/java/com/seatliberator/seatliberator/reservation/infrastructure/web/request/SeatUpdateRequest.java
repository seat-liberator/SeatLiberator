package com.seatliberator.seatliberator.reservation.infrastructure.web.request;

public record SeatUpdateRequest(
        String roomId,
        String seatId
) {
}
