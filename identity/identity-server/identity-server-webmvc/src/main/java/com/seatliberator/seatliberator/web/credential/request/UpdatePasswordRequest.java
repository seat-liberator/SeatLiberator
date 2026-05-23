package com.seatliberator.seatliberator.web.credential.request;

import com.seatliberator.seatliberator.identity.server.application.credential.port.in.command.UpdatePasswordCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "비밀번호 변경 요청")
public record UpdatePasswordRequest(
        @Schema(description = "사용자 Id", example = "00000000-0000-0000-0000-000000000001")
        @NotNull UUID userId,
        @Schema(description = "기존 비밀번호")
        @NotBlank String oldPassword,
        @Schema(description = "새 비밀번호")
        @NotBlank String newPassword
) {
    public UpdatePasswordCommand toCommand() {
        return UpdatePasswordCommand.of(userId, oldPassword, newPassword);
    }
}
