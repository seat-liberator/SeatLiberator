package com.seatliberator.seatliberator.identity.server.domain.token;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RefreshToken 도메인 테스트")
public class RefreshTokenTest {
    private static final String TOKEN_HASH = "token-hash";
    private static final String SUBJECT = "user-1";
    private static final Instant ISSUED_AT = Instant.parse("2026-05-28T00:00:00Z");
    private static final Duration TTL = Duration.ofDays(14);
    private static final Instant EXPIRES_AT = ISSUED_AT.plus(TTL);

    @Test
    @DisplayName("tokenHash가 null 또는 blank면 예외")
    void throw_exception_when_token_hash_is_null_or_blank() {
        assertThatDomainThrownBy(() -> RefreshToken.of(null, SUBJECT, ISSUED_AT, EXPIRES_AT))
                .hasNonNullMessageFor("tokenHash");

        assertThatDomainThrownBy(() -> RefreshToken.of("   ", SUBJECT, ISSUED_AT, EXPIRES_AT))
                .hasNonBlankMessageFor("tokenHash");
    }

    @Test
    @DisplayName("subject가 null 또는 blank면 예외")
    void throw_exception_when_subject_is_null_or_blank() {
        assertThatDomainThrownBy(() -> RefreshToken.of(TOKEN_HASH, null, ISSUED_AT, EXPIRES_AT))
                .hasNonNullMessageFor("subject");

        assertThatDomainThrownBy(() -> RefreshToken.of(TOKEN_HASH, "   ", ISSUED_AT, EXPIRES_AT))
                .hasNonBlankMessageFor("subject");
    }

    @Test
    @DisplayName("issuedAt 또는 expiresAt이 null이면 예외")
    void throw_exception_when_temporal_field_is_null() {
        assertThatDomainThrownBy(() -> RefreshToken.of(TOKEN_HASH, SUBJECT, null, EXPIRES_AT))
                .hasNonNullMessageFor("issuedAt");

        assertThatDomainThrownBy(() -> RefreshToken.of(TOKEN_HASH, SUBJECT, ISSUED_AT, null))
                .hasNonNullMessageFor("expiresAt");
    }

    @Test
    @DisplayName("발급 시 issuedAt과 ttl로 expiresAt을 계산한다")
    void issue_refresh_token() {
        var refreshToken = RefreshToken.issue(TOKEN_HASH, SUBJECT, ISSUED_AT, TTL);

        assertThat(refreshToken.getTokenHash()).isEqualTo(TOKEN_HASH);
        assertThat(refreshToken.getSubject()).isEqualTo(SUBJECT);
        assertThat(refreshToken.getIssuedAt()).isEqualTo(ISSUED_AT);
        assertThat(refreshToken.getExpiresAt()).isEqualTo(EXPIRES_AT);
    }

    @Test
    @DisplayName("만료 시각 전이면 만료되지 않은 것으로 판단한다")
    void not_expired_before_expires_at() {
        var refreshToken = RefreshToken.of(TOKEN_HASH, SUBJECT, ISSUED_AT, EXPIRES_AT);

        assertThat(refreshToken.isExpiresAt(EXPIRES_AT.minusNanos(1))).isFalse();
    }

    @Test
    @DisplayName("만료 시각과 같거나 이후이면 만료된 것으로 판단한다")
    void expired_at_or_after_expires_at() {
        var refreshToken = RefreshToken.of(TOKEN_HASH, SUBJECT, ISSUED_AT, EXPIRES_AT);

        assertThat(refreshToken.isExpiresAt(EXPIRES_AT)).isTrue();
        assertThat(refreshToken.isExpiresAt(EXPIRES_AT.plusNanos(1))).isTrue();
    }

    @Test
    @DisplayName("조회 시각 기준 잔여 ttl을 반환한다")
    void returns_remaining_ttl_at() {
        var refreshToken = RefreshToken.of(TOKEN_HASH, SUBJECT, ISSUED_AT, EXPIRES_AT);

        assertThat(refreshToken.remainingTtlAt(EXPIRES_AT.minus(Duration.ofHours(1))))
                .isEqualTo(Duration.ofHours(1));
    }

    @Test
    @DisplayName("만료된 token의 잔여 ttl은 0이다")
    void returns_zero_remaining_ttl_when_expired() {
        var refreshToken = RefreshToken.of(TOKEN_HASH, SUBJECT, ISSUED_AT, EXPIRES_AT);

        assertThat(refreshToken.remainingTtlAt(EXPIRES_AT)).isEqualTo(Duration.ZERO);
        assertThat(refreshToken.remainingTtlAt(EXPIRES_AT.plusNanos(1))).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("만료 판단 기준 시각이 null이면 예외")
    void throw_exception_when_expiration_check_time_is_null() {
        var refreshToken = RefreshToken.of(TOKEN_HASH, SUBJECT, ISSUED_AT, EXPIRES_AT);

        assertThatDomainThrownBy(() -> refreshToken.isExpiresAt(null))
                .hasNonNullMessageFor("at");

        assertThatDomainThrownBy(() -> refreshToken.remainingTtlAt(null))
                .hasNonNullMessageFor("at");
    }
}
