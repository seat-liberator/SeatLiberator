package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.handler;

import com.seatliberator.seatliberator.identity.infrastructure.security.ResponseWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class FederatedAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ResponseWriter responseWriter;

    @Override
    public void onAuthenticationFailure(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        responseWriter.write(
                response,
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
        );
    }
}
