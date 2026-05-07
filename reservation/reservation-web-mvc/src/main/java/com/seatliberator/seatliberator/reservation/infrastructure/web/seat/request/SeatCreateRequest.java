package com.seatliberator.seatliberator.reservation.infrastructure.web.seat.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좌석 생성 요청")
public record SeatCreateRequest(
        @Schema(description = "좌석 ID", example = "A1")
        String seatId
) {
}
