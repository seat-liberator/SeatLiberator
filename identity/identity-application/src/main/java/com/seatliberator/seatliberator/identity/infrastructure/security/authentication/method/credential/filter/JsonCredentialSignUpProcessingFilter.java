package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.filter;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.exception.AuthenticationProcessingException;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.request.CredentialSignUpRequest;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.CredentialSignUpAuthenticationToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
public class JsonCredentialSignUpProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper;

    public JsonCredentialSignUpProcessingFilter(
            RequestMatcher requestMatcher,
            ObjectMapper objectMapper
    ) {
        super(requestMatcher);
        this.objectMapper = objectMapper;
    }

    @Override
    public @Nullable Authentication attemptAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response
    ) throws AuthenticationException, IOException, ServletException {
        try {
            var body = objectMapper.readValue(request.getInputStream(), CredentialSignUpRequest.class);

            var displayName = body.nickname();
            var email = body.email();
            var password = body.password();

            var authentication = CredentialSignUpAuthenticationToken.unauthenticated(
                    displayName,
                    email,
                    password
            );

            log.debug("Processed email={}", email);

            return getAuthenticationManager().authenticate(authentication);
        } catch (Exception e) {
            throw new AuthenticationProcessingException("Error occurred while parsing sign up request body");
        }
    }
}
