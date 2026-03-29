package com.seatliberator.seatliberator.reservation.book.infrastructure.web.request;

public record SeatCreateRequest(
        String roomId,
        String seatId
) {
}
