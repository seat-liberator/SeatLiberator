package com.seatliberator.seatliberator.reservation.infrastructure.web.request;

public record SeatUpdateRequest(
        String oldRoomId,
        String oldSeatId,
        String newRoomId,
        String newSeatId
) {
}
