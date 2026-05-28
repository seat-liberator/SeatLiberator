package com.seatliberator.seatliberator.reservation.web.seat.request;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.CreateSeatCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "좌석 생성 요청")
public record SeatCreateRequest(
        @Schema(description = "새 좌석 Code", example = "A1")
        @NotBlank String seatCode
) {
    public CreateSeatCommand toCommand(UUID roomId) {
        return CreateSeatCommand.of(roomId, seatCode);
    }
}
