package com.seatliberator.seatliberator.identity.server.authentication;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleSerializer;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.AuthenticationFederatedUseCase;
import com.seatliberator.seatliberator.identity.server.application.authentication.service.AuthenticationFederatedService;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountReader;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountLookupCriteria;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleReader;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.identity.server.authentication.AuthenticationUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationFederatedUseCase 테스트")
public class AuthenticationFederatedUseCaseTest {
    @Mock
    FederatedAccountReader accountReader;

    @Mock
    UserReader userReader;

    @Mock
    UserGrantedRoleReader roleReader;

    @Mock
    NamespaceRoleSerializer formatter;

    AuthenticationFederatedUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new AuthenticationFederatedService(
                accountReader,
                userReader,
                roleReader,
                formatter
        );
    }

    @Test
    @DisplayName("federated 계정과 사용자를 조회하고 인증 결과를 반환한다")
    void find_account_and_user_and_return_authenticated_result() {
        when(accountReader.findByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID)))
                .thenReturn(Optional.of(federatedAccount()));
        when(userReader.findById(USER_ID)).thenReturn(Optional.of(user()));
        when(roleReader.findByUserId(USER_ID)).thenReturn(userGrantedRoles());
        when(formatter.serialize(any(NamespaceRole.class))).thenReturn(SCOPE);

        var result = useCase.authenticate(authenticationFederatedCommand());

        verify(accountReader).findByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID));
        verify(userReader).findById(USER_ID);
        verify(roleReader).findByUserId(USER_ID);
        verify(formatter).serialize(any(NamespaceRole.class));
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.nickname()).isEqualTo(NICKNAME);
        assertThat(result.scopes()).isEqualTo(SCOPES);
    }

    @Test
    @DisplayName("federated 계정이 없으면 예외")
    void throw_exception_when_account_not_found() {
        when(accountReader.findByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID)))
                .thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.authenticate(authenticationFederatedCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.ACCOUNT_NOT_FOUND);

        verifyNoInteractions(userReader, roleReader, formatter);
    }

    @Test
    @DisplayName("계정에 연결된 사용자가 없으면 예외")
    void throw_exception_when_user_not_found() {
        when(accountReader.findByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID)))
                .thenReturn(Optional.of(federatedAccount()));
        when(userReader.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.authenticate(authenticationFederatedCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.USER_NOT_FOUND);

        verifyNoInteractions(roleReader, formatter);
    }
}
