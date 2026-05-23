package com.seatliberator.seatliberator.identity.server.federated;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleFormatter;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.LinkFederatedAccountUseCase;
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
@DisplayName("LinkFederatedAccountUseCase 테스트")
public class LinkFederatedAccountUseCaseTest {
    @Mock
    FederatedAccountReader reader;

    @Mock
    FederatedAccountStore store;

    @Mock
    InitialRoleGrantor roleGrantor;

    @Mock
    NamespaceRoleFormatter formatter;

    @Mock
    UserReader userReader;

    @Mock
    UserCreator userCreator;

    LinkFederatedAccountUseCase useCase;

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
    @DisplayName("federated 계정 중복이 없고 사용자가 있으면 계정을 연결한다")
    void save_federated_account_when_account_is_available_and_user_exists() {
        when(reader.existsByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID)))
                .thenReturn(false);
        when(userReader.existsById(USER_ID)).thenReturn(true);

        useCase.link(linkFederatedAccountCommand());

        var captor = ArgumentCaptor.forClass(FederatedAccount.class);
        verify(reader).existsByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID));
        verify(userReader).existsById(USER_ID);
        verify(store).save(captor.capture());

        var savedAccount = captor.getValue();
        assertThat(savedAccount.getUserId()).isEqualTo(USER_ID);
        assertThat(savedAccount.getRegistrationId()).isEqualTo(REGISTRATION_ID);
        assertThat(savedAccount.getProviderUserId()).isEqualTo(PROVIDER_USER_ID);
        assertThat(savedAccount.getCreatedAt()).isEqualTo(CLOCK.instant());
    }

    @Test
    @DisplayName("federated 계정이 이미 있으면 예외")
    void throw_exception_when_account_already_exists() {
        when(reader.existsByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID)))
                .thenReturn(true);

        assertThatApplicationThrownBy(() -> useCase.link(linkFederatedAccountCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.ACCOUNT_ALREADY_EXISTS);

        verifyNoInteractions(store, userReader);
    }

    @Test
    @DisplayName("연결할 사용자가 없으면 예외")
    void throw_exception_when_user_not_found() {
        when(reader.existsByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID)))
                .thenReturn(false);
        when(userReader.existsById(USER_ID)).thenReturn(false);

        assertThatApplicationThrownBy(() -> useCase.link(linkFederatedAccountCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.USER_NOT_FOUND);

        verifyNoInteractions(store);
    }
}
