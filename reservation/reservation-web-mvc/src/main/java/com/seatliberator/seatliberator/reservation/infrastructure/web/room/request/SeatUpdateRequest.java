package com.seatliberator.seatliberator.reservation.infrastructure.web.room.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좌석 정보 변경 요청")
public record SeatUpdateRequest(
        @Schema(description = "좌석이 속할 새로운 방 ID", example = "study-room-2")
        String newRoomId,
        @Schema(description = "좌석의 새로운 ID", example = "A2")
        String newSeatId
) {
}
