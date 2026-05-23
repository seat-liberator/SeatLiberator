package com.seatliberator.seatliberator.identity.server.security.shared.response;

import com.seatliberator.seatliberator.identity.server.application.jwks.service.JwtProvider;
import com.seatliberator.seatliberator.identity.server.application.jwks.service.OpaqueTokenProvider;
import com.seatliberator.seatliberator.identity.server.security.shared.principal.TrustedPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class TokenResponseProcessor {
    private final JwtProvider jwtProvider;
    private final OpaqueTokenProvider opaqueTokenProvider;
    private final ResponseWriter responseWriter;

    public void process(@Nullable HttpServletResponse response, TrustedPrincipal principal) throws IOException {
        var subject = principal.userId().toString();
        var scopes = principal.scopes();

        Map<String, Object> attribute = new HashMap<>();
        attribute.put("subject", subject);
        attribute.put("scopes", scopes);

        var accessToken = jwtProvider.issue(attribute);
        var refreshToken = opaqueTokenProvider.issue(attribute);

        var payload = TokenPayload.of(accessToken, refreshToken);

        responseWriter.write(response, HttpStatus.OK, payload);
    }
}
