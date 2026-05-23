package com.seatliberator.seatliberator.identity.server.authentication;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleSerializer;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.AuthenticationCredentialUseCase;
import com.seatliberator.seatliberator.identity.server.application.authentication.service.AuthenticationCredentialService;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountReader;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountEmailCriteria;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleReader;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.seatliberator.seatliberator.identity.server.authentication.AuthenticationUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationCredentialUseCase 테스트")
public class AuthenticationCredentialUseCaseTest {
    @Mock
    CredentialAccountReader accountReader;

    @Mock
    UserReader userReader;

    @Mock
    UserGrantedRoleReader roleReader;

    @Mock
    NamespaceRoleSerializer formatter;

    @Mock
    PasswordEncoder passwordEncoder;

    AuthenticationCredentialUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new AuthenticationCredentialService(
                accountReader,
                userReader,
                roleReader,
                formatter,
                passwordEncoder
        );
    }

    @Test
    @DisplayName("credential 계정과 사용자를 조회하고 인증 결과를 반환한다")
    void find_account_and_user_and_return_authenticated_result() {
        when(accountReader.findByCriteria(CredentialAccountEmailCriteria.of(EMAIL)))
                .thenReturn(Optional.of(credentialAccount()));
        when(passwordEncoder.matches(PASSWORD, PASSWORD_HASH)).thenReturn(true);
        when(userReader.findById(USER_ID)).thenReturn(Optional.of(user()));
        when(roleReader.findByUserId(USER_ID)).thenReturn(userGrantedRoles());
        when(formatter.serialize(any(NamespaceRole.class))).thenReturn(SCOPE);

        var result = useCase.authenticate(authenticationCredentialCommand());

        verify(accountReader).findByCriteria(CredentialAccountEmailCriteria.of(EMAIL));
        verify(passwordEncoder).matches(PASSWORD, PASSWORD_HASH);
        verify(userReader).findById(USER_ID);
        verify(roleReader).findByUserId(USER_ID);
        verify(formatter).serialize(any(NamespaceRole.class));
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.nickname()).isEqualTo(NICKNAME);
        assertThat(result.scopes()).isEqualTo(SCOPES);
    }

    @Test
    @DisplayName("credential 계정이 없으면 예외")
    void throw_exception_when_account_not_found() {
        when(accountReader.findByCriteria(CredentialAccountEmailCriteria.of(EMAIL)))
                .thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.authenticate(authenticationCredentialCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.ACCOUNT_NOT_FOUND);

        verifyNoInteractions(userReader, roleReader, formatter, passwordEncoder);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외")
    void throw_exception_when_password_does_not_match() {
        when(accountReader.findByCriteria(CredentialAccountEmailCriteria.of(EMAIL)))
                .thenReturn(Optional.of(credentialAccount()));
        when(passwordEncoder.matches(PASSWORD, PASSWORD_HASH)).thenReturn(false);

        assertThatApplicationThrownBy(() -> useCase.authenticate(authenticationCredentialCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.AUTHENTICATION_FAILED);

        verifyNoInteractions(userReader, roleReader, formatter);
    }

    @Test
    @DisplayName("계정에 연결된 사용자가 없으면 예외")
    void throw_exception_when_user_not_found() {
        when(accountReader.findByCriteria(CredentialAccountEmailCriteria.of(EMAIL)))
                .thenReturn(Optional.of(credentialAccount()));
        when(passwordEncoder.matches(PASSWORD, PASSWORD_HASH)).thenReturn(true);
        when(userReader.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.authenticate(authenticationCredentialCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.USER_NOT_FOUND);

        verifyNoInteractions(roleReader, formatter);
    }
}
