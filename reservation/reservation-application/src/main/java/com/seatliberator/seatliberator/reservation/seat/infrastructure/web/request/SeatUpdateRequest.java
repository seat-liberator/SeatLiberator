package com.seatliberator.seatliberator.reservation.seat.infrastructure.web.request;

public record SeatUpdateRequest(
        String newRoomId,
        String newSeatId
) {
}
