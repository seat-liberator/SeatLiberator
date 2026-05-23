package com.seatliberator.seatliberator.identity.server.role;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.GrantRoleUseCase;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleReader;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleStore;
import com.seatliberator.seatliberator.identity.server.application.role.service.UserGrantedRoleCommandService;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.identity.server.role.RoleUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GrantRoleUseCase 테스트")
public class GrantRoleUseCaseTest {
    @Mock
    UserGrantedRoleReader reader;

    @Mock
    UserGrantedRoleStore store;

    @Mock
    UserReader userReader;

    GrantRoleUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new UserGrantedRoleCommandService(reader, store, userReader, CLOCK);
    }

    @Test
    @DisplayName("사용자가 있으면 namespaceRole을 부여하고 결과를 반환한다")
    void grant_namespace_role_when_user_exists() {
        when(userReader.existsById(USER_ID)).thenReturn(true);
        when(store.save(any(UserGrantedRole.class))).thenAnswer(invocation -> {
            var grantedRole = invocation.<UserGrantedRole>getArgument(0);
            stubId(grantedRole, GRANTED_ROLE_ID);
            return grantedRole;
        });

        var result = useCase.grant(grantRoleCommand());

        var captor = ArgumentCaptor.forClass(UserGrantedRole.class);
        verify(userReader).existsById(USER_ID);
        verify(store).save(captor.capture());
        verifyNoInteractions(reader);

        var savedGrant = captor.getValue();
        assertThat(savedGrant.getUserId()).isEqualTo(USER_ID);
        assertThat(savedGrant.getNamespaceRole().namespace().value()).isEqualTo(NAMESPACE_VALUE);
        assertThat(savedGrant.getNamespaceRole().role()).isEqualTo(ROLE);
        assertThat(savedGrant.getCreatedAt()).isEqualTo(CLOCK.instant());
        assertThat(result.grantedRoleId()).isEqualTo(GRANTED_ROLE_ID);
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.namespaceRole().namespace().value()).isEqualTo(NAMESPACE_VALUE);
        assertThat(result.namespaceRole().role()).isEqualTo(ROLE);
    }

    @Test
    @DisplayName("사용자가 없으면 예외")
    void throw_exception_when_user_not_found() {
        when(userReader.existsById(USER_ID)).thenReturn(false);

        assertThatApplicationThrownBy(() -> useCase.grant(grantRoleCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.USER_NOT_FOUND);

        verify(userReader).existsById(USER_ID);
        verifyNoInteractions(reader, store);
    }
}
