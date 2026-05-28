package com.seatliberator.seatliberator.identity.server.domain.token;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RefreshToken 도메인 테스트")
public class RefreshTokenTest {
    private static final String TOKEN_HASH = "token-hash";
    private static final String RENEWED_TOKEN_HASH = "renewed-token-hash";
    private static final String FAMILY_ID = "refresh-token-family-1";
    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final Instant ISSUED_AT = Instant.parse("2026-05-28T00:00:00Z");
    private static final Duration IDLE_TTL = Duration.ofDays(14);
    private static final Duration SESSION_TTL = Duration.ofDays(30);
    private static final Instant IDLE_EXPIRES_AT = ISSUED_AT.plus(IDLE_TTL);
    private static final Instant SESSION_EXPIRES_AT = ISSUED_AT.plus(SESSION_TTL);
    private static final Instant REVOKED_AT = ISSUED_AT.plus(Duration.ofDays(7));

    @Test
    @DisplayName("tokenHash가 null 또는 blank면 예외")
    void throw_exception_when_token_hash_is_null_or_blank() {
        assertThatDomainThrownBy(() -> RefreshToken.issue(null, FAMILY_ID, USER_ID, ISSUED_AT, IDLE_TTL, SESSION_TTL))
                .hasNonNullMessageFor("tokenHash");

        assertThatDomainThrownBy(() -> RefreshToken.issue("   ", FAMILY_ID, USER_ID, ISSUED_AT, IDLE_TTL, SESSION_TTL))
                .hasNonBlankMessageFor("tokenHash");
    }

    @Test
    @DisplayName("familyId가 null 또는 blank면 예외")
    void throw_exception_when_family_id_is_null_or_blank() {
        assertThatDomainThrownBy(() -> RefreshToken.issue(TOKEN_HASH, null, USER_ID, ISSUED_AT, IDLE_TTL, SESSION_TTL))
                .hasNonNullMessageFor("familyId");

        assertThatDomainThrownBy(() -> RefreshToken.issue(TOKEN_HASH, "   ", USER_ID, ISSUED_AT, IDLE_TTL, SESSION_TTL))
                .hasNonBlankMessageFor("familyId");
    }

    @Test
    @DisplayName("userId가 null이면 예외")
    void throw_exception_when_user_id_is_null() {
        assertThatDomainThrownBy(() -> RefreshToken.issue(TOKEN_HASH, FAMILY_ID, null, ISSUED_AT, IDLE_TTL, SESSION_TTL))
                .hasNonNullMessageFor("userId");
    }

    @Test
    @DisplayName("issuedAt 또는 ttl이 null이면 예외")
    void throw_exception_when_issue_temporal_field_is_null() {
        assertThatDomainThrownBy(() -> RefreshToken.issue(TOKEN_HASH, FAMILY_ID, USER_ID, null, IDLE_TTL, SESSION_TTL))
                .hasNonNullMessageFor("issuedAt");

        assertThatDomainThrownBy(() -> RefreshToken.issue(TOKEN_HASH, FAMILY_ID, USER_ID, ISSUED_AT, null, SESSION_TTL))
                .hasNonNullMessageFor("idleTtl");

        assertThatDomainThrownBy(() -> RefreshToken.issue(TOKEN_HASH, FAMILY_ID, USER_ID, ISSUED_AT, IDLE_TTL, null))
                .hasNonNullMessageFor("sessionTtl");
    }

    @Test
    @DisplayName("ttl이 0 이하이면 예외")
    void throw_exception_when_ttl_is_not_positive() {
        assertThatDomainThrownBy(() -> RefreshToken.issue(TOKEN_HASH, FAMILY_ID, USER_ID, ISSUED_AT, Duration.ZERO, SESSION_TTL))
                .hasPositiveMessageFor("idleTtl");

        assertThatDomainThrownBy(() -> RefreshToken.issue(TOKEN_HASH, FAMILY_ID, USER_ID, ISSUED_AT, Duration.ofNanos(-1), SESSION_TTL))
                .hasPositiveMessageFor("idleTtl");

        assertThatDomainThrownBy(() -> RefreshToken.issue(TOKEN_HASH, FAMILY_ID, USER_ID, ISSUED_AT, IDLE_TTL, Duration.ZERO))
                .hasPositiveMessageFor("sessionTtl");

        assertThatDomainThrownBy(() -> RefreshToken.issue(TOKEN_HASH, FAMILY_ID, USER_ID, ISSUED_AT, IDLE_TTL, Duration.ofNanos(-1)))
                .hasPositiveMessageFor("sessionTtl");
    }

    @Test
    @DisplayName("발급 시 idle/session 만료 시각을 계산한다")
    void issue_refresh_token() {
        var refreshToken = refreshToken();

        assertThat(refreshToken.getTokenHash()).isEqualTo(TOKEN_HASH);
        assertThat(refreshToken.getFamilyId()).isEqualTo(FAMILY_ID);
        assertThat(refreshToken.getUserId()).isEqualTo(USER_ID);
        assertThat(refreshToken.getIssuedAt()).isEqualTo(ISSUED_AT);
        assertThat(refreshToken.getSessionIssuedAt()).isEqualTo(ISSUED_AT);
        assertThat(refreshToken.getIdleExpiresAt()).isEqualTo(IDLE_EXPIRES_AT);
        assertThat(refreshToken.getSessionExpiresAt()).isEqualTo(SESSION_EXPIRES_AT);
        assertThat(refreshToken.getRevokedAt()).isNull();
    }

    @Test
    @DisplayName("idle ttl이 session ttl보다 길면 session 만료 시각으로 idle 만료 시각을 제한한다")
    void caps_idle_expiration_at_session_expiration_when_issue_idle_ttl_exceeds_session_ttl() {
        var refreshToken = RefreshToken.issue(
                TOKEN_HASH,
                FAMILY_ID,
                USER_ID,
                ISSUED_AT,
                SESSION_TTL.plusNanos(1),
                SESSION_TTL
        );

        assertThat(refreshToken.getIdleExpiresAt()).isEqualTo(SESSION_EXPIRES_AT);
        assertThat(refreshToken.getSessionExpiresAt()).isEqualTo(SESSION_EXPIRES_AT);
    }

    @Test
    @DisplayName("갱신 시 family/session 정보는 유지하고 tokenHash와 idle 만료 시각을 갱신한다")
    void renew_refresh_token() {
        var refreshToken = refreshToken();
        var renewedAt = ISSUED_AT.plus(Duration.ofDays(1));

        var renewed = refreshToken.renew(RENEWED_TOKEN_HASH, renewedAt, IDLE_TTL);

        assertThat(renewed.getTokenHash()).isEqualTo(RENEWED_TOKEN_HASH);
        assertThat(renewed.getFamilyId()).isEqualTo(FAMILY_ID);
        assertThat(renewed.getUserId()).isEqualTo(USER_ID);
        assertThat(renewed.getIssuedAt()).isEqualTo(renewedAt);
        assertThat(renewed.getSessionIssuedAt()).isEqualTo(ISSUED_AT);
        assertThat(renewed.getIdleExpiresAt()).isEqualTo(renewedAt.plus(IDLE_TTL));
        assertThat(renewed.getSessionExpiresAt()).isEqualTo(SESSION_EXPIRES_AT);
        assertThat(renewed.getRevokedAt()).isNull();
    }

    @Test
    @DisplayName("폐기 시 revokedAt을 기록하고 기존 token 정보를 유지한다")
    void revoke_refresh_token() {
        var refreshToken = refreshToken();

        var revoked = refreshToken.revoke(REVOKED_AT);

        assertThat(revoked.getTokenHash()).isEqualTo(TOKEN_HASH);
        assertThat(revoked.getFamilyId()).isEqualTo(FAMILY_ID);
        assertThat(revoked.getUserId()).isEqualTo(USER_ID);
        assertThat(revoked.getIssuedAt()).isEqualTo(ISSUED_AT);
        assertThat(revoked.getSessionIssuedAt()).isEqualTo(ISSUED_AT);
        assertThat(revoked.getIdleExpiresAt()).isEqualTo(IDLE_EXPIRES_AT);
        assertThat(revoked.getSessionExpiresAt()).isEqualTo(SESSION_EXPIRES_AT);
        assertThat(revoked.getRevokedAt()).isEqualTo(REVOKED_AT);
    }

    @Test
    @DisplayName("폐기 시각이 null이면 예외")
    void throw_exception_when_revoked_at_is_null() {
        var refreshToken = refreshToken();

        assertThatDomainThrownBy(() -> refreshToken.revoke(null))
                .hasNonNullMessageFor("revokedAt");
    }

    @Test
    @DisplayName("폐기 시각이 발급 시각보다 이전이면 예외")
    void throw_exception_when_revoked_at_is_before_issued_at() {
        var refreshToken = refreshToken();

        assertThatThrownBy(() -> refreshToken.revoke(ISSUED_AT.minusNanos(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("revokedAt must not be before issuedAt.");
    }

    @Test
    @DisplayName("폐기 예정 토큰을 갱신하면 revokedAt을 유지한다")
    void renew_preserves_revoked_at() {
        var refreshToken = refreshToken().revoke(REVOKED_AT);
        var renewedAt = ISSUED_AT.plus(Duration.ofDays(1));

        var renewed = refreshToken.renew(RENEWED_TOKEN_HASH, renewedAt, IDLE_TTL);

        assertThat(renewed.getRevokedAt()).isEqualTo(REVOKED_AT);
    }

    @Test
    @DisplayName("갱신 시 tokenHash가 null 또는 blank면 예외")
    void throw_exception_when_renewed_token_hash_is_null_or_blank() {
        var refreshToken = refreshToken();

        assertThatDomainThrownBy(() -> refreshToken.renew(null, ISSUED_AT.plus(Duration.ofDays(1)), IDLE_TTL))
                .hasNonNullMessageFor("tokenHash");

        assertThatDomainThrownBy(() -> refreshToken.renew("   ", ISSUED_AT.plus(Duration.ofDays(1)), IDLE_TTL))
                .hasNonBlankMessageFor("tokenHash");
    }

    @Test
    @DisplayName("갱신 시각 또는 idle ttl이 유효하지 않으면 예외")
    void throw_exception_when_renew_temporal_field_is_invalid() {
        var refreshToken = refreshToken();

        assertThatDomainThrownBy(() -> refreshToken.renew(RENEWED_TOKEN_HASH, null, IDLE_TTL))
                .hasNonNullMessageFor("renewedAt");

        assertThatDomainThrownBy(() -> refreshToken.renew(RENEWED_TOKEN_HASH, ISSUED_AT.plus(Duration.ofDays(1)), null))
                .hasNonNullMessageFor("idleTtl");

        assertThatDomainThrownBy(() -> refreshToken.renew(RENEWED_TOKEN_HASH, ISSUED_AT.plus(Duration.ofDays(1)), Duration.ZERO))
                .hasPositiveMessageFor("idleTtl");
    }

    @Test
    @DisplayName("갱신 시각이 세션 발급 시각보다 이전이면 예외")
    void throw_exception_when_renewed_at_is_before_session_issued_at() {
        var refreshToken = refreshToken();

        assertThatThrownBy(() -> refreshToken.renew(RENEWED_TOKEN_HASH, ISSUED_AT.minusNanos(1), IDLE_TTL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("issuedAt must not be before sessionIssuedAt.");
    }

    @Test
    @DisplayName("갱신 후 idle 만료 시각이 session 만료 시각보다 이후이면 session 만료 시각으로 제한한다")
    void caps_renewed_idle_expiration_at_session_expiration() {
        var refreshToken = refreshToken();

        var renewed = refreshToken.renew(
                RENEWED_TOKEN_HASH,
                IDLE_EXPIRES_AT.minusNanos(1),
                SESSION_TTL
        );

        assertThat(renewed.getIdleExpiresAt()).isEqualTo(SESSION_EXPIRES_AT);
        assertThat(renewed.getSessionExpiresAt()).isEqualTo(SESSION_EXPIRES_AT);
    }

    @Test
    @DisplayName("만료된 token은 갱신할 수 없다")
    void throw_exception_when_renewing_expired_token() {
        var refreshToken = refreshToken();

        assertThatThrownBy(() -> refreshToken.renew(RENEWED_TOKEN_HASH, IDLE_EXPIRES_AT, IDLE_TTL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expired refresh token cannot be renewed.");
    }

    @Test
    @DisplayName("폐기된 token은 갱신할 수 없다")
    void throw_exception_when_renewing_revoked_token() {
        var refreshToken = refreshToken().revoke(REVOKED_AT);

        assertThatThrownBy(() -> refreshToken.renew(RENEWED_TOKEN_HASH, REVOKED_AT, IDLE_TTL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expired refresh token cannot be renewed.");
    }

    @Test
    @DisplayName("idle 만료 시각 전이면 만료되지 않은 것으로 판단한다")
    void not_expired_before_idle_expires_at() {
        var refreshToken = refreshToken();

        assertThat(refreshToken.isExpiredAt(IDLE_EXPIRES_AT.minusNanos(1))).isFalse();
    }

    @Test
    @DisplayName("idle 만료 시각과 같거나 이후이면 만료된 것으로 판단한다")
    void expired_at_or_after_idle_expires_at() {
        var refreshToken = refreshToken();

        assertThat(refreshToken.isExpiredAt(IDLE_EXPIRES_AT)).isTrue();
        assertThat(refreshToken.isExpiredAt(IDLE_EXPIRES_AT.plusNanos(1))).isTrue();
    }

    @Test
    @DisplayName("폐기 시각과 같거나 이후이면 만료된 것으로 판단한다")
    void expired_at_or_after_revoked_at() {
        var refreshToken = refreshToken().revoke(REVOKED_AT);

        assertThat(refreshToken.isExpiredAt(REVOKED_AT.minusNanos(1))).isFalse();
        assertThat(refreshToken.isExpiredAt(REVOKED_AT)).isTrue();
        assertThat(refreshToken.isExpiredAt(REVOKED_AT.plusNanos(1))).isTrue();
    }

    @Test
    @DisplayName("조회 시각 기준 잔여 ttl을 반환한다")
    void returns_remaining_ttl_at() {
        var refreshToken = refreshToken();

        assertThat(refreshToken.remainingTtlAt(IDLE_EXPIRES_AT.minus(Duration.ofHours(1))))
                .isEqualTo(Duration.ofHours(1));
    }

    @Test
    @DisplayName("만료된 token의 잔여 ttl은 0이다")
    void returns_zero_remaining_ttl_when_expired() {
        var refreshToken = refreshToken();

        assertThat(refreshToken.remainingTtlAt(IDLE_EXPIRES_AT)).isEqualTo(Duration.ZERO);
        assertThat(refreshToken.remainingTtlAt(IDLE_EXPIRES_AT.plusNanos(1))).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("폐기된 token의 잔여 ttl은 revokedAt 기준으로 계산한다")
    void returns_remaining_ttl_at_revoked_at() {
        var refreshToken = refreshToken().revoke(REVOKED_AT);

        assertThat(refreshToken.remainingTtlAt(REVOKED_AT.minus(Duration.ofHours(1))))
                .isEqualTo(Duration.ofHours(1));
        assertThat(refreshToken.remainingTtlAt(REVOKED_AT)).isEqualTo(Duration.ZERO);
        assertThat(refreshToken.remainingTtlAt(REVOKED_AT.plusNanos(1))).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("만료 판단 기준 시각이 null이면 예외")
    void throw_exception_when_expiration_check_time_is_null() {
        var refreshToken = refreshToken();

        assertThatDomainThrownBy(() -> refreshToken.isExpiredAt(null))
                .hasNonNullMessageFor("at");

        assertThatDomainThrownBy(() -> refreshToken.remainingTtlAt(null))
                .hasNonNullMessageFor("at");
    }

    private RefreshToken refreshToken() {
        return RefreshToken.issue(TOKEN_HASH, FAMILY_ID, USER_ID, ISSUED_AT, IDLE_TTL, SESSION_TTL);
    }
}
