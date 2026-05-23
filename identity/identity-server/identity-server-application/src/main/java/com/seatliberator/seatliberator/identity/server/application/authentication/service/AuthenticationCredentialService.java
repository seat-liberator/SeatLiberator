package com.seatliberator.seatliberator.identity.server.application.authentication.service;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleSerializer;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.AuthenticationCredentialUseCase;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.command.AuthenticationCredentialCommand;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountReader;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountEmailCriteria;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleReader;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationCredentialService implements AuthenticationCredentialUseCase {
    private final CredentialAccountReader accountReader;
    private final UserReader userReader;
    private final UserGrantedRoleReader roleReader;
    private final NamespaceRoleSerializer formatter;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthenticatedResult authenticate(AuthenticationCredentialCommand command) {
        var email = command.email();

        var criteria = CredentialAccountEmailCriteria.of(email);
        var account = accountReader.findByCriteria(criteria)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.ACCOUNT_NOT_FOUND));

        var password = command.password();
        var matches = passwordEncoder.matches(password, account.getPasswordHash());
        if (!matches) throw new IdentityApplicationException(IdentityApplicationErrorCode.AUTHENTICATION_FAILED);

        var userId = account.getUserId();
        var user = userReader.findById(userId)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.USER_NOT_FOUND));
        var scopes = roleReader.findByUserId(userId).stream()
                .map(grant -> formatter.serialize(grant.getNamespaceRole()))
                .collect(Collectors.toUnmodifiableSet());
        return AuthenticatedResult.from(user.getId(), user.getNickname(), scopes);
    }
}
