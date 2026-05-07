package com.seatliberator.seatliberator.reservation.infrastructure.web.seat.request;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.UpdateSeatTimeSlotCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

@Schema(description = "좌석 시간 슬롯 변경 요청")
public record SeatTimeSlotUpdateRequest(
        @Schema(description = "기존 좌석 시간 슬롯의 새로운 시작 시간. HH:mm:ss 형식", type = "string", format = "time", example = "13:00:00")
        LocalTime startAt,
        @Schema(description = "기존 시간 슬롯의 새로운 지속 시간. ISO-8601 Duration 형식", type = "string", example = "PT30M")
        Duration duration
) {
    public UpdateSeatTimeSlotCommand toCommand(UUID seatTimeSlotId) {
        return new UpdateSeatTimeSlotCommand(seatTimeSlotId, startAt, duration);
    }
}
