package com.seatliberator.seatliberator.identity.server.domain.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static com.seatliberator.seatliberator.identity.server.domain.account.AccountTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FederatedAccount 테스트")
public class FederatedAccountTest {
    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        @Test
        @DisplayName("userId, registrationId, providerUserId, createdAt으로 생성한다")
        void create_with_user_id_registration_id_provider_user_id_and_created_at() {
            var account = FederatedAccount.of(USER_ID, REGISTRATION_ID, PROVIDER_USER_ID, USER_CREATED_AT);

            assertThat(account.getUserId()).isEqualTo(USER_ID);
            assertThat(account.getRegistrationId()).isEqualTo(REGISTRATION_ID);
            assertThat(account.getProviderUserId()).isEqualTo(PROVIDER_USER_ID);
            assertThat(account.getCreatedAt()).isEqualTo(USER_CREATED_AT);
        }

        @ParameterizedTest(name = "userId = {0}")
        @NullSource
        @DisplayName("userId가 null이면 예외")
        void throw_exception_when_null_user_id(UUID userId) {
            assertThatDomainThrownBy(() -> FederatedAccount.of(userId, REGISTRATION_ID, PROVIDER_USER_ID, USER_CREATED_AT))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("userId");
        }

        @ParameterizedTest(name = "registrationId = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("registrationId가 빈 문자열이면 예외")
        void throw_exception_when_blank_registration_id(String registrationId) {
            assertThatDomainThrownBy(() -> FederatedAccount.of(USER_ID, registrationId, PROVIDER_USER_ID, USER_CREATED_AT))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasNonBlankMessageFor("registrationId");
        }

        @ParameterizedTest(name = "registrationId = {0}")
        @NullSource
        @DisplayName("registrationId가 null이면 예외")
        void throw_exception_when_null_registration_id(String registrationId) {
            assertThatDomainThrownBy(() -> FederatedAccount.of(USER_ID, registrationId, PROVIDER_USER_ID, USER_CREATED_AT))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("registrationId");
        }

        @ParameterizedTest(name = "providerUserId = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("providerUserId가 빈 문자열이면 예외")
        void throw_exception_when_blank_provider_user_id(String providerUserId) {
            assertThatDomainThrownBy(() -> FederatedAccount.of(USER_ID, REGISTRATION_ID, providerUserId, USER_CREATED_AT))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasNonBlankMessageFor("providerUserId");
        }

        @ParameterizedTest(name = "providerUserId = {0}")
        @NullSource
        @DisplayName("providerUserId가 null이면 예외")
        void throw_exception_when_null_provider_user_id(String providerUserId) {
            assertThatDomainThrownBy(() -> FederatedAccount.of(USER_ID, REGISTRATION_ID, providerUserId, USER_CREATED_AT))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("providerUserId");
        }

        @ParameterizedTest(name = "createdAt = {0}")
        @NullSource
        @DisplayName("createdAt이 null이면 예외")
        void throw_exception_when_null_created_at(Instant createdAt) {
            assertThatDomainThrownBy(() -> FederatedAccount.of(USER_ID, REGISTRATION_ID, PROVIDER_USER_ID, createdAt))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("createdAt");
        }
    }
}
