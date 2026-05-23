package com.seatliberator.seatliberator.web.role.request;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.UpdateRoleCommand;
import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "권한 수정 요청")
public record UpdateRoleRequest(
        @Schema(description = "사용자 Id", example = "00000000-0000-0000-0000-000000000001")
        @NotNull UUID userId,
        @Schema(description = "서비스 명", example = "identity")
        @NotBlank String namespace,
        @Schema(description = "새로운 역할", example = "MAINTAINER")
        @NotNull Role role
) {
    public UpdateRoleCommand toCommand() {
        return UpdateRoleCommand.of(userId, SimpleApplicationNamespace.of(namespace), role);
    }
}
