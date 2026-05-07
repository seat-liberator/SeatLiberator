package com.seatliberator.seatliberator.reservation.web.book.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "예약 변경 요청")
public record ReservationUpdateRequest(
        @Schema(description = "예약한 좌석의 방 ID", example = "study-room-1")
        String roomId,
        @Schema(description = "예약한 좌석 ID", example = "A1")
        String seatId,
        @Schema(description = "예약 시작 시간", example = "2026-01-01T13:00Z")
        Instant startAt,
        @Schema(description = "예약 종료 시간", example = "2026-01-01T14:30Z")
        Instant endAt
) {
}
