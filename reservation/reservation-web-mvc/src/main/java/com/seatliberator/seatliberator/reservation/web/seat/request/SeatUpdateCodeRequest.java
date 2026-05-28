package com.seatliberator.seatliberator.reservation.web.seat.request;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.UpdateSeatCodeCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "좌석 Code 정보 변경 요청")
public record SeatUpdateCodeRequest(
        @Schema(description = "좌석의 새로운 Code", example = "A2")
        @NotBlank String newCode
) {
    public UpdateSeatCodeCommand toCommand(UUID seatId) {
        return UpdateSeatCodeCommand.of(seatId, newCode);
    }
}
