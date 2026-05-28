package com.seatliberator.seatliberator.reservation.web.room.request;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateRoomCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "방 생성 요청")
public record CreateRoomRequest(
        @Schema(description = "방 Code", example = "study-room-1")
        @NotBlank String roomCode
) {
    public CreateRoomCommand toCommand() {
        return CreateRoomCommand.of(roomCode);
    }
}
