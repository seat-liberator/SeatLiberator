package com.seatliberator.seatliberator.reservation.web.booking.request;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CancelBookingCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "예약 취소 요청")
public record CancelBookingRequest(
        @Schema(description = "취소할 예약 Id", example = "00000000-0000-0000-000000000001")
        @NotNull UUID reservationId
) {
    public CancelBookingCommand toCommand() {
        return CancelBookingCommand.of(reservationId);
    }
}
