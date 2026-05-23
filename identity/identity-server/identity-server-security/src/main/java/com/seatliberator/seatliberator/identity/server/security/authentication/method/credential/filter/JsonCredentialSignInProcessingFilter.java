package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.filter;

import com.seatliberator.seatliberator.identity.server.security.shared.exception.AuthenticationProcessingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
public class JsonCredentialSignInProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper;

    public JsonCredentialSignInProcessingFilter(
            RequestMatcher requestMatcher,
            ObjectMapper objectMapper
    ) {
        super(requestMatcher);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response
    ) throws AuthenticationException, IOException, ServletException {
        final CredentialSignInAuthentication authentication;
        try {
            var body = objectMapper.readValue(request.getInputStream(), CredentialSignInRequest.class);
            authentication = CredentialSignInAuthentication.of(body.email(), body.password());
        } catch (Exception e) {
            throw new AuthenticationProcessingException("Invalid credential sign-in request.");
        }

        return getAuthenticationManager().authenticate(authentication);
    }
}
