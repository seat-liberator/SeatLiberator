package com.seatliberator.seatliberator.reservation.web.waitlist.request;

import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistBehavior;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "대기열 등록 요청")
public record CreateWaitlistRequest(
        @Schema(description = "대기열을 등록할 좌석 시간 슬롯 ID 목록")
        List<UUID> seatTimeSlotIds,
        @Schema(description = "좌석 점유일", example = "2026-01-01")
        LocalDate occupancyDate,
        @Schema(description = "좌석이 비었을 때 처리 방식", example = "AUTO_CLAIM")
        WaitlistBehavior behavior
) {
}
