package com.seatliberator.seatliberator.web.role.request;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.RevokeRoleCommand;
import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "권한 회수 요청")
public record RevokeRoleRequest(
        @Schema(description = "사용자 Id", example = "00000000-0000-0000-0000-000000000001")
        @NotNull UUID userId,
        @Schema(description = "서비스 명", example = "identity")
        @NotBlank String namespace
) {
    public RevokeRoleCommand toCommand() {
        return new RevokeRoleCommand(userId, SimpleApplicationNamespace.of(namespace));
    }
}
