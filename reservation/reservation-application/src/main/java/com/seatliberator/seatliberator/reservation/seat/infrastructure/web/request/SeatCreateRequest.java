package com.seatliberator.seatliberator.reservation.seat.infrastructure.web.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좌석 생성 요청")
public record SeatCreateRequest(
        @Schema(description = "좌석 ID", example = "A-1")
        String seatId
) {
}
