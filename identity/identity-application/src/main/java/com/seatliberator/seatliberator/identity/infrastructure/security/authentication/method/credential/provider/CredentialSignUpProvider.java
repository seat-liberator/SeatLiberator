package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.provider;

import com.seatliberator.seatliberator.identity.application.port.in.UserRegistrar;
import com.seatliberator.seatliberator.identity.application.port.in.command.RegistrationCommand;
import com.seatliberator.seatliberator.identity.core.token.TokenProvider;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.CredentialSignUpAuthenticationToken;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.TrustedAuthenticationToken;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.credential.token.TrustedUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CredentialSignUpProvider implements AuthenticationProvider {
    private final UserRegistrar userRegistrar;
    private final TokenProvider jwtProvider;

    @Override
    public @Nullable Authentication authenticate(
            @NonNull Authentication authentication
    ) throws AuthenticationException {
        var signUpToken = (CredentialSignUpAuthenticationToken) authentication;

        var nickname = signUpToken.getNickname();
        var email = signUpToken.getEmail();
        var password = (String) signUpToken.getCredentials();

        var registrationCommand = new RegistrationCommand.Credential(
                nickname,
                email,
                password
        );
        var authEntry = userRegistrar.register(registrationCommand);

        var trustedUserPrincipal = new TrustedUserPrincipal(
                authEntry.userId().toString(),
                authEntry.nickname()
        );

        return new TrustedAuthenticationToken(
                trustedUserPrincipal,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return CredentialSignUpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
