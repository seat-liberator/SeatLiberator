package com.seatliberator.seatliberator.identity.server.token;

import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.token.internal.AccessTokenIssuer;
import com.seatliberator.seatliberator.identity.server.application.token.internal.RefreshTokenRenewer;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.RefreshAccessTokenUseCase;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenRotationResult;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenRotator;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenStore;
import com.seatliberator.seatliberator.identity.server.application.token.service.RefreshAccessTokenService;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.identity.server.token.TokenUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshAccessTokenUseCase 테스트")
public class RefreshAccessTokenUseCaseTest {
    @Mock
    RefreshTokenStore refreshTokenStore;

    @Mock
    RefreshTokenRotator refreshTokenRotator;

    @Mock
    UserReader userReader;

    @Mock
    AccessTokenIssuer accessTokenIssuer;

    @Mock
    RefreshTokenRenewer refreshTokenRenewer;

    RefreshAccessTokenUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new RefreshAccessTokenService(
                refreshTokenStore,
                refreshTokenRotator,
                userReader,
                accessTokenIssuer,
                refreshTokenRenewer,
                CLOCK
        );
    }

    @Test
    @DisplayName("refresh token을 회전하고 새 access token과 refresh token을 반환한다")
    void rotate_refresh_token_and_return_new_tokens() {
        var oldRefreshToken = refreshToken();
        var renewedRefreshToken = renewedRefreshToken();
        when(refreshTokenStore.findActiveByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(oldRefreshToken));
        when(refreshTokenRenewer.renewRefreshToken(oldRefreshToken, CLOCK.instant())).thenReturn(renewedRefreshToken);
        when(refreshTokenRotator.rotate(oldRefreshToken, renewedRefreshToken, CLOCK.instant()))
                .thenReturn(RefreshTokenRotationResult.SUCCESS);
        when(accessTokenIssuer.issue(USER_ID, CLOCK.instant())).thenReturn(accessToken());

        var result = useCase.refresh(refreshAccessTokenCommand());

        verify(refreshTokenStore).findActiveByTokenHash(REFRESH_TOKEN_HASH);
        verify(refreshTokenRenewer).renewRefreshToken(oldRefreshToken, CLOCK.instant());
        verify(refreshTokenRotator).rotate(oldRefreshToken, renewedRefreshToken, CLOCK.instant());
        verify(accessTokenIssuer).issue(USER_ID, CLOCK.instant());
        verifyNoInteractions(userReader);

        assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(RENEWED_REFRESH_TOKEN_HASH);
    }

    @Test
    @DisplayName("활성 refresh token이 없으면 예외")
    void throw_exception_when_active_refresh_token_not_found() {
        when(refreshTokenStore.findActiveByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.refresh(refreshAccessTokenCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.AUTHENTICATION_FAILED);

        verifyNoInteractions(refreshTokenRenewer, refreshTokenRotator, accessTokenIssuer, userReader);
    }

    @Test
    @DisplayName("refresh token 회전에 실패하면 예외")
    void throw_exception_when_refresh_token_rotation_fails() {
        var oldRefreshToken = refreshToken();
        var renewedRefreshToken = renewedRefreshToken();
        when(refreshTokenStore.findActiveByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(oldRefreshToken));
        when(refreshTokenRenewer.renewRefreshToken(oldRefreshToken, CLOCK.instant())).thenReturn(renewedRefreshToken);
        when(refreshTokenRotator.rotate(oldRefreshToken, renewedRefreshToken, CLOCK.instant()))
                .thenReturn(RefreshTokenRotationResult.NEW_TOKEN_CONFLICT);

        assertThatApplicationThrownBy(() -> useCase.refresh(refreshAccessTokenCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.AUTHENTICATION_FAILED);

        verifyNoInteractions(accessTokenIssuer, userReader);
    }
}
