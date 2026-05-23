package com.seatliberator.seatliberator.identity.server.credential;

import com.seatliberator.seatliberator.identity.server.application.credential.port.in.RegisterCredentialAccountUseCase;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountReader;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountStore;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountEmailCriteria;
import com.seatliberator.seatliberator.identity.server.application.credential.service.CredentialAccountCommandService;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.ScopeReader;
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

import static com.seatliberator.seatliberator.identity.server.credential.CredentialUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterCredentialAccountUseCase 테스트")
public class RegisterCredentialAccountUseCaseTest {
    @Mock
    CredentialAccountReader reader;

    @Mock
    CredentialAccountStore store;

    @Mock
    ScopeReader scopeReader;

    @Mock
    UserCreator userCreator;

    @Mock
    PasswordEncoder passwordEncoder;

    RegisterCredentialAccountUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new CredentialAccountCommandService(
                reader,
                store,
                scopeReader,
                userCreator,
                passwordEncoder,
                CLOCK
        );
    }

    @Test
    @DisplayName("이메일 중복이 없으면 사용자를 생성하고 credential 계정을 저장한다")
    void create_user_and_save_credential_account_when_email_is_available() {
        when(reader.existsByCriteria(CredentialAccountEmailCriteria.of(EMAIL))).thenReturn(false);
        when(userCreator.create(NICKNAME)).thenReturn(user());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD_HASH);
        when(scopeReader.readScopes(USER_ID.toString())).thenReturn(SCOPES);

        var result = useCase.register(registerCredentialAccountCommand());

        var captor = ArgumentCaptor.forClass(CredentialAccount.class);
        verify(reader).existsByCriteria(CredentialAccountEmailCriteria.of(EMAIL));
        verify(userCreator).create(NICKNAME);
        verify(passwordEncoder).encode(PASSWORD);
        verify(store).save(captor.capture());
        verify(scopeReader).readScopes(USER_ID.toString());

        var savedAccount = captor.getValue();
        assertThat(savedAccount.getUserId()).isEqualTo(USER_ID);
        assertThat(savedAccount.getEmail()).isEqualTo(EMAIL);
        assertThat(savedAccount.getPasswordHash()).isEqualTo(ENCODED_PASSWORD_HASH);
        assertThat(savedAccount.getCreatedAt()).isEqualTo(CLOCK.instant());
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.nickname()).isEqualTo(NICKNAME);
        assertThat(result.scopes()).isEqualTo(SCOPES);
    }

    @Test
    @DisplayName("email이 중복되면 예외")
    void throw_exception_when_email_is_duplicated() {
        when(reader.existsByCriteria(CredentialAccountEmailCriteria.of(EMAIL))).thenReturn(true);

        assertThatApplicationThrownBy(() -> useCase.register(registerCredentialAccountCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.EMAIL_DUPLICATED);

        verifyNoInteractions(store, userCreator, passwordEncoder, scopeReader);
    }
}
