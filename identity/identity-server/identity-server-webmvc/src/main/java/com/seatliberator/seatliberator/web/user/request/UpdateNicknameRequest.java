package com.seatliberator.seatliberator.web.user.request;

import com.seatliberator.seatliberator.identity.server.application.user.port.in.command.UpdateNicknameCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "닉네임 변경 요청")
public record UpdateNicknameRequest(
        @Schema(description = "새로운 닉네임", example = "new_nickname")
        @NotBlank String nickname
) {
    public UpdateNicknameCommand toCommand(UUID userId) {
        return UpdateNicknameCommand.of(userId, nickname);
    }
}
