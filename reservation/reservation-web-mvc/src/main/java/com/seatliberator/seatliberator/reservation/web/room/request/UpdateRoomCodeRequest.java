package com.seatliberator.seatliberator.reservation.web.room.request;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomCodeCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "방 Code 정보 변경 요청")
public record UpdateRoomCodeRequest(
        @Schema(description = "새로운 방 Code", example = "study-room-2")
        @NotBlank String newCode
) {
    public UpdateRoomCodeCommand toCommand(UUID roomId) {
        return UpdateRoomCodeCommand.of(roomId, newCode);
    }
}