package com.seatliberator.seatliberator.reservation.web.booking.request;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateBookingCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "예약 생성 요청")
public record CreateBookingRequest(
        @Schema(description = "예약할 좌석 슬롯 Id 목록")
        @NotEmpty List<@NotNull UUID> slotIds,
        @Schema(
                type = "string",
                format = "date",
                example = "2025-05-18",
                description = "예약할 날짜"
        )
        @NotNull LocalDate occupancyDate
) {
    public CreateBookingCommand toCommand(String userId) {
        return CreateBookingCommand.of(userId, slotIds, occupancyDate);
    }
}
