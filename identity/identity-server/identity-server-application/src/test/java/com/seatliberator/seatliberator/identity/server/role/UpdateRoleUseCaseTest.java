package com.seatliberator.seatliberator.identity.server.role;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.UpdateRoleUseCase;
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

import java.util.Optional;

import static com.seatliberator.seatliberator.identity.server.role.RoleUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateRoleUseCase 테스트")
public class UpdateRoleUseCaseTest {
    @Mock
    UserGrantedRoleReader reader;

    @Mock
    UserGrantedRoleStore store;

    @Mock
    UserReader userReader;

    UpdateRoleUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new UserGrantedRoleCommandService(reader, store, userReader, CLOCK);
    }

    @Test
    @DisplayName("사용자와 namespace로 권한을 조회하고 role을 변경한다")
    void find_grant_by_user_and_namespace_and_update_role() {
        var grantedRole = userGrantedRole();
        var criteria = userGrantedRoleUserNamespaceCriteria();
        when(userReader.existsById(USER_ID)).thenReturn(true);
        when(reader.findByCriteria(criteria)).thenReturn(Optional.of(grantedRole));
        when(store.save(any(UserGrantedRole.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.update(updateRoleCommand());

        var captor = ArgumentCaptor.forClass(UserGrantedRole.class);
        verify(userReader).existsById(USER_ID);
        verify(reader).findByCriteria(criteria);
        verify(store).save(captor.capture());

        var savedGrant = captor.getValue();
        assertThat(savedGrant.getNamespaceRole().namespace().value()).isEqualTo(NAMESPACE_VALUE);
        assertThat(savedGrant.getNamespaceRole().role()).isEqualTo(UPDATED_ROLE);
        assertThat(result.grantedRoleId()).isEqualTo(GRANTED_ROLE_ID);
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.namespaceRole().namespace().value()).isEqualTo(NAMESPACE_VALUE);
        assertThat(result.namespaceRole().role()).isEqualTo(UPDATED_ROLE);
    }

    @Test
    @DisplayName("사용자가 없으면 예외")
    void throw_exception_when_user_not_found() {
        when(userReader.existsById(USER_ID)).thenReturn(false);

        assertThatApplicationThrownBy(() -> useCase.update(updateRoleCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.USER_NOT_FOUND);

        verify(userReader).existsById(USER_ID);
        verifyNoInteractions(reader, store);
    }

    @Test
    @DisplayName("변경할 권한이 없으면 예외")
    void throw_exception_when_grant_not_found() {
        var criteria = userGrantedRoleUserNamespaceCriteria();
        when(userReader.existsById(USER_ID)).thenReturn(true);
        when(reader.findByCriteria(criteria)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.update(updateRoleCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.GRANT_NOT_FOUND);

        verify(userReader).existsById(USER_ID);
        verify(reader).findByCriteria(criteria);
        verifyNoInteractions(store);
    }
}
