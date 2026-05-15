package com.seatliberator.seatliberator.reservation.web.room.request;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomOperationPolicyCommand;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailySchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "방 운영 정책 변경 요청")
public record UpdateRoomOperationPolicyRequest(
        @Schema(description = "1인당 최대 예약 가능 횟수", example = "5")
        @NotNull
        Integer maxReservationPerUser,
        @Schema(description = "최대 예약 가능 시간. ISO-8601 Duration 형식", type = "string", example = "PT3H")
        @NotNull
        Duration maxReservationDuration,
        @Schema(description = "방 운영 상태", example = "OPEN")
        @NotNull
        RoomOperationStatus operationStatus,
        @Schema(description = "하루 내 방 운영 시간 구간 목록")
        @Valid
        @NotEmpty
        List<OperationScheduleRequest> operationSchedule
) {
    public UpdateRoomOperationPolicyCommand toCommand(String roomId) {
        return new UpdateRoomOperationPolicyCommand(
                roomId,
                maxReservationPerUser,
                maxReservationDuration,
                operationStatus,
                SimpleDailySchedule.of(
                        operationSchedule.stream()
                                .map(OperationScheduleRequest::toRange)
                                .toList()
                )
        );
    }

    @Schema(description = "하루 내 방 운영 시간 구간")
    public record OperationScheduleRequest(
            @Schema(description = "구간 시작 시간. HH:mm:ss 형식", type = "string", format = "time", example = "09:00:00")
            @NotNull
            LocalTime startAt,
            @Schema(description = "구간 운영 시간. ISO-8601 Duration 형식", type = "string", example = "PT3H")
            @NotNull
            Duration duration
    ) {
        DailyNanoRange toRange() {
            return SimpleDailyNanoRange.of(startAt, duration);
        }
    }
}
