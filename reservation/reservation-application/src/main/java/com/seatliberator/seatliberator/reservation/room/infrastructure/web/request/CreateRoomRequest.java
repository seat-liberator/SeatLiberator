package com.seatliberator.seatliberator.reservation.room.infrastructure.web.request;

import com.seatliberator.seatliberator.reservation.room.application.port.in.command.CreateRoomCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "방 생성 요청")
public record CreateRoomRequest(
        @Schema(description = "방 ID", example = "study-room-1")
        @NotBlank String roomId
) {
    public CreateRoomCommand toCommand() {
        return new CreateRoomCommand(roomId);
    }
}
