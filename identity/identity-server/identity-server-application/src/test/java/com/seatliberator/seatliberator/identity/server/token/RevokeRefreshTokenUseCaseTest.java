package com.seatliberator.seatliberator.identity.server.token;

import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.RevokeRefreshTokenUseCase;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenStore;
import com.seatliberator.seatliberator.identity.server.application.token.service.RevokeRefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.identity.server.token.TokenUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RevokeRefreshTokenUseCase 테스트")
public class RevokeRefreshTokenUseCaseTest {
    @Mock
    RefreshTokenStore refreshTokenStore;

    RevokeRefreshTokenUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new RevokeRefreshTokenService(refreshTokenStore, CLOCK);
    }

    @Test
    @DisplayName("활성 refresh token을 조회하고 폐기한다")
    void find_active_refresh_token_and_revoke() {
        var refreshToken = refreshToken();
        when(refreshTokenStore.findActiveByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(refreshToken));

        useCase.revoke(revokeRefreshTokenCommand());

        verify(refreshTokenStore).findActiveByTokenHash(REFRESH_TOKEN_HASH);
        verify(refreshTokenStore).revoke(refreshToken, CLOCK.instant());
    }

    @Test
    @DisplayName("활성 refresh token이 없으면 예외")
    void throw_exception_when_active_refresh_token_not_found() {
        when(refreshTokenStore.findActiveByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.revoke(revokeRefreshTokenCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.AUTHENTICATION_FAILED);

        verify(refreshTokenStore, never()).revoke(any(), any());
    }
}
