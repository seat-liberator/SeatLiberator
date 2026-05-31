package com.seatliberator.seatliberator.identity.server.token;

import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleReader;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.application.token.internal.AccessTokenIssuer;
import com.seatliberator.seatliberator.identity.server.application.token.internal.RefreshTokenFactory;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.CreateAccessTokenUseCase;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenStore;
import com.seatliberator.seatliberator.identity.server.application.token.service.CreateAccessTokenService;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.seatliberator.seatliberator.identity.server.token.TokenUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAccessTokenUseCase 테스트")
public class CreateAccessTokenUseCaseTest {
    @Mock
    RefreshTokenStore refreshTokenStore;

    @Mock
    AccessTokenIssuer accessTokenIssuer;

    @Mock
    UserReader userReader;

    @Mock
    UserGrantedRoleReader grantedRoleReader;

    @Mock
    RefreshTokenFactory refreshTokenFactory;

    CreateAccessTokenUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new CreateAccessTokenService(
                refreshTokenStore,
                accessTokenIssuer,
                userReader,
                grantedRoleReader,
                refreshTokenFactory,
                CLOCK
        );
    }

    @Test
    @DisplayName("access token을 발급하고 refresh token을 저장한 뒤 결과를 반환한다")
    void issue_access_token_and_save_refresh_token() {
        var accessToken = accessToken();
        var refreshToken = refreshToken();
        when(accessTokenIssuer.issue(USER_ID, CLOCK.instant())).thenReturn(accessToken);
        when(refreshTokenFactory.create(anyString(), eq(USER_ID), eq(CLOCK.instant()))).thenReturn(refreshToken);

        var result = useCase.create(createAccessTokenCommand());

        var familyIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(accessTokenIssuer).issue(USER_ID, CLOCK.instant());
        verify(refreshTokenFactory).create(familyIdCaptor.capture(), eq(USER_ID), eq(CLOCK.instant()));
        verify(refreshTokenStore).save(refreshToken, CLOCK.instant());
        verifyNoInteractions(userReader, grantedRoleReader);

        assertThat(UUID.fromString(familyIdCaptor.getValue()).toString()).isEqualTo(familyIdCaptor.getValue());
        assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN_HASH);
    }

    @Test
    @DisplayName("access token 발급이 실패하면 refresh token을 생성하지 않는다")
    void do_not_create_refresh_token_when_access_token_issue_fails() {
        when(accessTokenIssuer.issue(USER_ID, CLOCK.instant()))
                .thenThrow(new IdentityApplicationException(IdentityApplicationErrorCode.USER_NOT_FOUND));

        assertThatApplicationThrownBy(() -> useCase.create(createAccessTokenCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.USER_NOT_FOUND);

        verifyNoInteractions(refreshTokenFactory, refreshTokenStore);
    }
}
