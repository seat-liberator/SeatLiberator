package com.seatliberator.seatliberator.reservation.seat.infrastructure.web.request;

public record SeatUpdateRequest(
        String oldRoomId,
        String oldSeatId,
        String newRoomId,
        String newSeatId
) {
}
