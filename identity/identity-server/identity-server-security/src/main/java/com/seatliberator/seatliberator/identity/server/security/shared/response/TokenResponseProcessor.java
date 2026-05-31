package com.seatliberator.seatliberator.identity.server.security.shared.response;

import com.seatliberator.seatliberator.identity.server.application.token.port.in.CreateAccessTokenUseCase;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.command.CreateAccessTokenCommand;
import com.seatliberator.seatliberator.identity.server.security.shared.principal.TrustedPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenResponseProcessor {
    private final CreateAccessTokenUseCase useCase;
    private final ResponseWriter responseWriter;

    public void process(@Nullable HttpServletResponse response, TrustedPrincipal principal) throws IOException {
        var userId = principal.userId();

        var command = CreateAccessTokenCommand.of(userId);
        var result = useCase.create(command);
        var payload = TokenPayload.of(result.accessToken(), result.refreshToken());
        responseWriter.write(response, HttpStatus.OK, payload);
    }
}
