package com.seatliberator.seatliberator.reservation.web.waitlist.request;

import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistBehavior;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "대기열 등록 요청")
public record CreateWaitlistRequest(
        @Schema(description = "대기열을 등록할 방 ID", example = "room-1")
        String roomId,
        @Schema(description = "대기열을 등록할 좌석 ID", example = "A-1")
        String seatId,
        @Schema(description = "희망 예약 시작 시간", example = "2026-01-01T13:00Z")
        Instant startAt,
        @Schema(description = "희망 예약 종료 시간", example = "2026-01-01T14:30Z")
        Instant endAt,
        @Schema(description = "좌석이 비었을 때 처리 방식", example = "AUTO_CLAIM")
        WaitlistBehavior behavior
) {
}
