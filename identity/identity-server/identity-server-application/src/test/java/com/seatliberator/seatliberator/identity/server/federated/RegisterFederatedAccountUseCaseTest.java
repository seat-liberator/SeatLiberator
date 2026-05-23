package com.seatliberator.seatliberator.identity.server.federated;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleSerializer;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.RegisterFederatedAccountUseCase;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountReader;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountStore;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountLookupCriteria;
import com.seatliberator.seatliberator.identity.server.application.federated.service.FederatedAccountCommandService;
import com.seatliberator.seatliberator.identity.server.application.role.contract.InitialRoleGrantor;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.user.contract.UserCreator;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import com.seatliberator.seatliberator.identity.server.domain.account.FederatedAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.identity.server.federated.FederatedUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterFederatedAccountUseCase 테스트")
public class RegisterFederatedAccountUseCaseTest {
    @Mock
    FederatedAccountReader reader;

    @Mock
    FederatedAccountStore store;

    @Mock
    InitialRoleGrantor roleGrantor;

    @Mock
    NamespaceRoleSerializer formatter;

    @Mock
    UserReader userReader;

    @Mock
    UserCreator userCreator;

    RegisterFederatedAccountUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new FederatedAccountCommandService(
                reader,
                store,
                userReader,
                userCreator,
                roleGrantor,
                formatter,
                CLOCK
        );
    }

    @Test
    @DisplayName("federated 계정 중복이 없으면 사용자를 생성하고 계정을 저장한다")
    void create_user_and_save_federated_account_when_account_is_available() {
        when(reader.existsByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID)))
                .thenReturn(false);
        when(userCreator.create(PROVIDER_USER_NICKNAME)).thenReturn(user());
        when(roleGrantor.grantInitial(USER_ID)).thenReturn(userGrantedRoles());
        when(formatter.serialize(any(NamespaceRole.class))).thenReturn(SCOPE);

        var result = useCase.register(registerFederatedAccountCommand());

        var captor = ArgumentCaptor.forClass(FederatedAccount.class);
        verify(reader).existsByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID));
        verify(userCreator).create(PROVIDER_USER_NICKNAME);
        verify(store).save(captor.capture());
        verify(roleGrantor).grantInitial(USER_ID);
        verify(formatter).serialize(any(NamespaceRole.class));

        var savedAccount = captor.getValue();
        assertThat(savedAccount.getUserId()).isEqualTo(USER_ID);
        assertThat(savedAccount.getRegistrationId()).isEqualTo(REGISTRATION_ID);
        assertThat(savedAccount.getProviderUserId()).isEqualTo(PROVIDER_USER_ID);
        assertThat(savedAccount.getCreatedAt()).isEqualTo(CLOCK.instant());
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.nickname()).isEqualTo(NICKNAME);
        assertThat(result.scopes()).isEqualTo(SCOPES);
    }

    @Test
    @DisplayName("federated 계정이 이미 있으면 예외")
    void throw_exception_when_account_already_exists() {
        when(reader.existsByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID)))
                .thenReturn(true);

        assertThatApplicationThrownBy(() -> useCase.register(registerFederatedAccountCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.ACCOUNT_ALREADY_EXISTS);

        verifyNoInteractions(store, userCreator, roleGrantor, formatter);
    }
}
