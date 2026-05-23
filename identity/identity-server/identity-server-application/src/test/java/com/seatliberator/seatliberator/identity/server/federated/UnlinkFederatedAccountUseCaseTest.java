package com.seatliberator.seatliberator.identity.server.federated;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleFormatter;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.UnlinkFederatedAccountUseCase;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountReader;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountStore;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountUserRegistrationLookupCriteria;
import com.seatliberator.seatliberator.identity.server.application.federated.service.FederatedAccountCommandService;
import com.seatliberator.seatliberator.identity.server.application.role.contract.InitialRoleGrantor;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.user.contract.UserCreator;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.identity.server.federated.FederatedUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnlinkFederatedAccountUseCase 테스트")
public class UnlinkFederatedAccountUseCaseTest {
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

    UnlinkFederatedAccountUseCase useCase;

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
    @DisplayName("사용자와 registrationId로 계정을 조회하고 삭제한다")
    void find_account_by_user_and_registration_id_and_delete() {
        var account = federatedAccount();
        when(reader.findByCriteria(FederatedAccountUserRegistrationLookupCriteria.of(USER_ID, REGISTRATION_ID)))
                .thenReturn(Optional.of(account));

        useCase.unlink(unlinkFederatedAccountCommand());

        verify(reader).findByCriteria(FederatedAccountUserRegistrationLookupCriteria.of(USER_ID, REGISTRATION_ID));
        verify(store).delete(account);
    }

    @Test
    @DisplayName("삭제할 federated 계정이 없으면 예외")
    void throw_exception_when_account_not_found() {
        when(reader.findByCriteria(FederatedAccountUserRegistrationLookupCriteria.of(USER_ID, REGISTRATION_ID)))
                .thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.unlink(unlinkFederatedAccountCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.ACCOUNT_NOT_FOUND);

        verifyNoInteractions(store);
    }
}
