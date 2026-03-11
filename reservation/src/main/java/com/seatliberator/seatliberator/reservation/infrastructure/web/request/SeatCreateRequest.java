package com.seatliberator.seatliberator.reservation.infrastructure.web.request;

public record SeatCreateRequest(
        String roomId,
        String seatId
) {
}
