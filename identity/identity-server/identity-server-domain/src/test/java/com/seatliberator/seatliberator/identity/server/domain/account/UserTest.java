package com.seatliberator.seatliberator.identity.server.domain.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static com.seatliberator.seatliberator.identity.server.domain.account.AccountTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User 테스트")
public class UserTest {
    private User createUser() {
        return new UserFixture.Builder()
                .nickname(USER_NICKNAME)
                .createdAt(USER_CREATED_AT)
                .build();
    }

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        @Test
        @DisplayName("nickname과 createdAt으로 생성한다")
        void create_with_nickname_and_created_at() {
            var user = User.of(USER_NICKNAME, USER_CREATED_AT);

            assertThat(user.getNickname()).isEqualTo(USER_NICKNAME);
            assertThat(user.getCreatedAt()).isEqualTo(USER_CREATED_AT);
            assertThat(user.getUpdatedAt()).isNull();
        }

        @ParameterizedTest(name = "nickname = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("nickname이 빈 문자열이면 예외")
        void throw_exception_when_blank_nickname(String nickname) {
            assertThatDomainThrownBy(() -> User.of(nickname, USER_CREATED_AT))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasNonBlankMessageFor("nickname");
        }

        @ParameterizedTest(name = "nickname = {0}")
        @NullSource
        @DisplayName("nickname이 null이면 예외")
        void throw_exception_when_null_nickname(String nickname) {
            assertThatDomainThrownBy(() -> User.of(nickname, USER_CREATED_AT))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("nickname");
        }

        @ParameterizedTest(name = "createdAt = {0}")
        @NullSource
        @DisplayName("createdAt이 null이면 예외")
        void throw_exception_when_null_created_at(Instant createdAt) {
            assertThatDomainThrownBy(() -> User.of(USER_NICKNAME, createdAt))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("createdAt");
        }
    }

    @Nested
    @DisplayName("닉네임 변경 테스트")
    class UpdateNicknameTest {
        @Test
        @DisplayName("nickname을 변경한다")
        void update_nickname() {
            var user = createUser();

            user.updateNickname(UPDATED_USER_NICKNAME, USER_UPDATED_AT);

            assertThat(user.getNickname()).isEqualTo(UPDATED_USER_NICKNAME);
            assertThat(user.getUpdatedAt()).isEqualTo(USER_UPDATED_AT);
        }

        @ParameterizedTest(name = "nickname = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("변경할 nickname이 빈 문자열이면 예외")
        void throw_exception_when_update_nickname_is_blank(String nickname) {
            var user = createUser();

            assertThatDomainThrownBy(() -> user.updateNickname(nickname, USER_UPDATED_AT))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasNonBlankMessageFor("nickname");
        }

        @ParameterizedTest(name = "nickname = {0}")
        @NullSource
        @DisplayName("변경할 nickname이 null이면 예외")
        void throw_exception_when_update_nickname_is_null(String nickname) {
            var user = createUser();

            assertThatDomainThrownBy(() -> user.updateNickname(nickname, USER_UPDATED_AT))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("nickname");
        }

        @ParameterizedTest(name = "updatedAt = {0}")
        @NullSource
        @DisplayName("updatedAt이 null이면 예외")
        void throw_exception_when_update_nickname_updated_at_is_null(Instant updatedAt) {
            var user = createUser();

            assertThatDomainThrownBy(() -> user.updateNickname(UPDATED_USER_NICKNAME, updatedAt))
                    .isInstanceOf(NullPointerException.class)
                    .hasNonNullMessageFor("updatedAt");
        }

        @Test
        @DisplayName("updatedAt이 createdAt보다 이전이면 예외")
        void throw_exception_when_update_nickname_updated_at_is_before_created_at() {
            var user = createUser();
            var updatedAt = USER_CREATED_AT.minusSeconds(1);

            assertThatDomainThrownBy(() -> user.updateNickname(UPDATED_USER_NICKNAME, updatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("updatedAt")
                    .hasMessageContaining("createdAt");
        }
    }
}
