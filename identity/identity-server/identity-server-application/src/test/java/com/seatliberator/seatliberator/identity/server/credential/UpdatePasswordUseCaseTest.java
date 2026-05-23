package com.seatliberator.seatliberator.identity.server.credential;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleFormatter;
import com.seatliberator.seatliberator.identity.server.application.credential.port.in.UpdatePasswordUseCase;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountReader;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountStore;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountUserCriteria;
import com.seatliberator.seatliberator.identity.server.application.credential.service.CredentialAccountCommandService;
import com.seatliberator.seatliberator.identity.server.application.role.contract.InitialRoleGrantor;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.user.contract.UserCreator;
import com.seatliberator.seatliberator.identity.server.domain.account.CredentialAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.seatliberator.seatliberator.identity.server.credential.CredentialUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdatePasswordUseCase 테스트")
public class UpdatePasswordUseCaseTest {
    @Mock
    CredentialAccountReader reader;

    @Mock
    CredentialAccountStore store;

    @Mock
    InitialRoleGrantor roleGrantor;

    @Mock
    NamespaceRoleFormatter formatter;

    @Mock
    UserCreator userCreator;

    @Mock
    PasswordEncoder passwordEncoder;

    UpdatePasswordUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new CredentialAccountCommandService(
                reader,
                store,
                userCreator,
                roleGrantor,
                formatter,
                passwordEncoder,
                CLOCK
        );
    }

    @Test
    @DisplayName("계정을 조회하고 기존 비밀번호가 일치하면 비밀번호를 변경한다")
    void find_account_and_update_password_when_old_password_matches() {
        when(reader.findByCriteria(CredentialAccountUserCriteria.of(USER_ID)))
                .thenReturn(Optional.of(credentialAccount()));
        when(passwordEncoder.matches(OLD_PASSWORD, PASSWORD_HASH)).thenReturn(true);

        useCase.update(updatePasswordCommand());

        var captor = ArgumentCaptor.forClass(CredentialAccount.class);
        verify(reader).findByCriteria(CredentialAccountUserCriteria.of(USER_ID));
        verify(passwordEncoder).matches(OLD_PASSWORD, PASSWORD_HASH);
        verify(store).save(captor.capture());

        var savedAccount = captor.getValue();
        assertThat(savedAccount.getPasswordHash()).isEqualTo(NEW_PASSWORD);
        assertThat(savedAccount.getUpdatedAt()).isEqualTo(CLOCK.instant());
    }

    @Test
    @DisplayName("credential 계정이 없으면 예외")
    void throw_exception_when_account_not_found() {
        when(reader.findByCriteria(CredentialAccountUserCriteria.of(USER_ID)))
                .thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.update(updatePasswordCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.ACCOUNT_NOT_FOUND);

        verifyNoInteractions(store, passwordEncoder);
    }

    @Test
    @DisplayName("기존 비밀번호가 일치하지 않으면 예외")
    void throw_exception_when_old_password_does_not_match() {
        when(reader.findByCriteria(CredentialAccountUserCriteria.of(USER_ID)))
                .thenReturn(Optional.of(credentialAccount()));
        when(passwordEncoder.matches(OLD_PASSWORD, PASSWORD_HASH)).thenReturn(false);

        assertThatApplicationThrownBy(() -> useCase.update(updatePasswordCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.AUTHENTICATION_FAILED);

        verifyNoInteractions(store);
    }
}
