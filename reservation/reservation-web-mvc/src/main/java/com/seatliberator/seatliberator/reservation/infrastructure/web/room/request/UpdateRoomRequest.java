package com.seatliberator.seatliberator.reservation.infrastructure.web.room.request;

import com.seatliberator.seatliberator.reservation.room.application.port.in.command.UpdateRoomCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "방 정보 변경 요청")
public record UpdateRoomRequest(
        @Schema(description = "새로운 방 ID", example = "study-room-2")
        @NotBlank String newRoomId
) {
    public UpdateRoomCommand toCommand(String oldRoomId) {
        return new UpdateRoomCommand(oldRoomId, newRoomId);
    }
}