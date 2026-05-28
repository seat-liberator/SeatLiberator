package com.seatliberator.seatliberator.reservation.web.seat.request;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.CreateSeatTimeSlotCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

@Schema(description = "좌석 시간 슬롯 생성 요청")
public record SeatTimeSlotCreateRequest(
        @Schema(description = "좌석 시간 슬롯 시작 시간. HH:mm:ss 형식", type = "string", format = "time", example = "13:00:00")
        @NotNull LocalTime startAt,
        @Schema(description = "시간 슬롯 지속 시간. ISO-8601 Duration 형식", type = "string", example = "PT30M")
        @NotNull Duration duration
) {
    public CreateSeatTimeSlotCommand toCommand(UUID seatId) {
        return CreateSeatTimeSlotCommand.of(seatId, startAt, duration);
    }
}
