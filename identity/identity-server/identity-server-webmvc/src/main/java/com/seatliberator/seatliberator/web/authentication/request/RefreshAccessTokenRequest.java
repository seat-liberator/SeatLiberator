package com.seatliberator.seatliberator.web.authentication.request;

import com.seatliberator.seatliberator.identity.server.application.token.port.in.command.RefreshAccessTokenCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 재발급 요청")
public record RefreshAccessTokenRequest(
        @Schema(description = "재발급 티켓")
        @NotBlank String refreshToken
) {
    public RefreshAccessTokenCommand toCommand() {
        return RefreshAccessTokenCommand.of(refreshToken);
    }
}
