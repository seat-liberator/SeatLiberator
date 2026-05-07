package com.seatliberator.seatliberator.reservation.web.book.request;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleInstantRange;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "예약 생성 요청")
public record ReservationCreateRequest(
        @Schema(description = "예약 시작 시간", example = "2026-01-01T13:00Z")
        Instant startAt,
        @Schema(description = "예약 종료 시간", example = "2026-01-01T14:30Z")
        Instant endAt
) {
    public CreateReservationCommand toCommand(String userId, SeatLocator locator, Actor requester) {
        var range = SimpleInstantRange.of(startAt, endAt);
        return CreateReservationCommand.of(userId, locator, range, requester);
    }
}
