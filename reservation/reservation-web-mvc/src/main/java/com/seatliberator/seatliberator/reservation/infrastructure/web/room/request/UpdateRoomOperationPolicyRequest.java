package com.seatliberator.seatliberator.reservation.infrastructure.web.room.request;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomOperationPolicyCommand;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;
import java.time.LocalTime;

@Schema(description = "방 운영 정책 변경 요청")
public record UpdateRoomOperationPolicyRequest(
        @Schema(description = "1인당 최대 예약 가능 횟수", example = "5")
        Integer maxReservationPerUser,
        @Schema(description = "최대 예약 가능 시간. ISO-8601 Duration 형식", type = "string", example = "PT3H")
        Duration maxReservationDuration,
        @Schema(description = "방 운영 상태", example = "OPEN")
        RoomOperationStatus operationStatus,
        @Schema(description = "방 운영 시작 시간. HH:mm:ss 형식", type = "string", format = "time", example = "09:00:00")
        LocalTime openAt,
        @Schema(description = "방 운영 종료 시간. HH:mm:ss 형식", type = "string", format = "time", example = "22:00:00")
        LocalTime closeAt
) {
    public UpdateRoomOperationPolicyCommand toCommand(String roomId) {
        return new UpdateRoomOperationPolicyCommand(
                roomId,
                maxReservationPerUser,
                maxReservationDuration,
                operationStatus,
                openAt,
                closeAt
        );
    }
}
