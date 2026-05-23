package com.seatliberator.seatliberator.web.role.request;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.GrantRoleCommand;
import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "권한 부여 요청")
public record GrantRoleRequest(
        @Schema(description = "사용자 Id", example = "00000000-0000-0000-0000-000000000001")
        @NotNull UUID userId,
        @Schema(description = "서비스 명", example = "identity")
        @NotBlank String namespace,
        @Schema(description = "역할", example = "USER")
        @NotNull Role role
) {
    public GrantRoleCommand toCommand() {
        var namespaceRole = SimpleNamespaceRole.from(SimpleApplicationNamespace.of(namespace), role);
        return GrantRoleCommand.of(userId, namespaceRole);
    }
}
