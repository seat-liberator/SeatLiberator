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

@DisplayName("CredentialAccount 테스트")
public class CredentialAccountTest {
    private CredentialAccount createCredentialAccount() {
        return new CredentialAccountFixture.Builder()
                .userId(USER_ID)
                .email(EMAIL)
                .passwordHash(PASSWORD_HASH)
                .createdAt(USER_CREATED_AT)
                .build();
    }

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        @Test
        @DisplayName("userId, email, passwordHash, createdAt으로 생성한다")
        void create_with_user_id_email_password_hash_and_created_at() {
            var account = CredentialAccount.of(USER_ID, EMAIL, PASSWORD_HASH, USER_CREATED_AT);

            assertThat(account.getUserId()).isEqualTo(USER_ID);
            assertThat(account.getEmail()).isEqualTo(EMAIL);
            assertThat(account.getPasswordHash()).isEqualTo(PASSWORD_HASH);
            assertThat(account.getCreatedAt()).isEqualTo(USER_CREATED_AT);
            assertThat(account.getUpdatedAt()).isNull();
        }

        @ParameterizedTest(name = "userId = {0}")
        @NullSource
        @DisplayName("userId가 null이면 예외")
        void throw_exception_when_null_user_id(UUID userId) {
            assertThatDomainThrownBy(() -> CredentialAccount.of(userId, EMAIL, PASSWORD_HASH, USER_CREATED_AT))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("userId");
        }

        @ParameterizedTest(name = "email = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("email이 빈 문자열이면 예외")
        void throw_exception_when_blank_email(String email) {
            assertThatDomainThrownBy(() -> CredentialAccount.of(USER_ID, email, PASSWORD_HASH, USER_CREATED_AT))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasNonBlankMessageFor("email");
        }

        @ParameterizedTest(name = "email = {0}")
        @NullSource
        @DisplayName("email이 null이면 예외")
        void throw_exception_when_null_email(String email) {
            assertThatDomainThrownBy(() -> CredentialAccount.of(USER_ID, email, PASSWORD_HASH, USER_CREATED_AT))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("email");
        }

        @ParameterizedTest(name = "passwordHash = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("passwordHash가 빈 문자열이면 예외")
        void throw_exception_when_blank_password_hash(String passwordHash) {
            assertThatDomainThrownBy(() -> CredentialAccount.of(USER_ID, EMAIL, passwordHash, USER_CREATED_AT))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasNonBlankMessageFor("passwordHash");
        }

        @ParameterizedTest(name = "passwordHash = {0}")
        @NullSource
        @DisplayName("passwordHash가 null이면 예외")
        void throw_exception_when_null_password_hash(String passwordHash) {
            assertThatDomainThrownBy(() -> CredentialAccount.of(USER_ID, EMAIL, passwordHash, USER_CREATED_AT))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("passwordHash");
        }

        @ParameterizedTest(name = "createdAt = {0}")
        @NullSource
        @DisplayName("createdAt이 null이면 예외")
        void throw_exception_when_null_created_at(Instant createdAt) {
            assertThatDomainThrownBy(() -> CredentialAccount.of(USER_ID, EMAIL, PASSWORD_HASH, createdAt))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("createdAt");
        }
    }

    @Nested
    @DisplayName("비밀번호 해시 변경 테스트")
    class UpdatePasswordHashTest {
        @Test
        @DisplayName("passwordHash를 변경한다")
        void update_password_hash() {
            var account = createCredentialAccount();

            account.updatePasswordHash(UPDATED_PASSWORD_HASH, USER_UPDATED_AT);

            assertThat(account.getPasswordHash()).isEqualTo(UPDATED_PASSWORD_HASH);
            assertThat(account.getUpdatedAt()).isEqualTo(USER_UPDATED_AT);
        }

        @ParameterizedTest(name = "passwordHash = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("변경할 passwordHash가 빈 문자열이면 예외")
        void throw_exception_when_update_password_hash_is_blank(String passwordHash) {
            var account = createCredentialAccount();

            assertThatDomainThrownBy(() -> account.updatePasswordHash(passwordHash, USER_UPDATED_AT))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasNonBlankMessageFor("passwordHash");
        }

        @ParameterizedTest(name = "passwordHash = {0}")
        @NullSource
        @DisplayName("변경할 passwordHash가 null이면 예외")
        void throw_exception_when_update_password_hash_is_null(String passwordHash) {
            var account = createCredentialAccount();

            assertThatDomainThrownBy(() -> account.updatePasswordHash(passwordHash, USER_UPDATED_AT))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("passwordHash");
        }

        @ParameterizedTest(name = "updatedAt = {0}")
        @NullSource
        @DisplayName("updatedAt이 null이면 예외")
        void throw_exception_when_update_password_hash_updated_at_is_null(Instant updatedAt) {
            var account = createCredentialAccount();

            assertThatDomainThrownBy(() -> account.updatePasswordHash(UPDATED_PASSWORD_HASH, updatedAt))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("updatedAt");
        }

        @Test
        @DisplayName("updatedAt이 createdAt보다 이전이면 예외")
        void throw_exception_when_update_password_hash_updated_at_is_before_created_at() {
            var account = createCredentialAccount();
            var updatedAt = USER_CREATED_AT.minusSeconds(1);

            assertThatDomainThrownBy(() -> account.updatePasswordHash(UPDATED_PASSWORD_HASH, updatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("updatedAt")
                    .hasMessageContaining("createdAt");
        }
    }
}
