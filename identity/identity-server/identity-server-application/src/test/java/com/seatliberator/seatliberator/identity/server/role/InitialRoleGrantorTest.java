package com.seatliberator.seatliberator.identity.server.role;

import com.seatliberator.seatliberator.identity.core.role.InitialNamespaceRoleRegistry;
import com.seatliberator.seatliberator.identity.server.application.role.contract.InitialRoleGrantor;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleStore;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.seatliberator.seatliberator.identity.server.role.RoleUseCaseTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InitialRoleGrantor 테스트")
public class InitialRoleGrantorTest {
    @Mock
    UserGrantedRoleStore store;

    @Mock
    InitialNamespaceRoleRegistry registry;

    @Mock
    UserReader userReader;

    InitialRoleGrantor grantor;

    @BeforeEach
    void run() {
        grantor = new InitialRoleGrantor(store, registry, userReader, CLOCK);
    }

    @Test
    @DisplayName("초기 namespaceRole을 사용자에게 부여한다")
    void grant_initial_namespace_roles_to_user() {
        var savedGrants = new AtomicReference<Collection<UserGrantedRole>>();
        when(registry.getAll()).thenReturn(List.of(NAMESPACE_ROLE));
        when(store.saveAll(anyCollection())).thenAnswer(invocation -> {
            Collection<UserGrantedRole> grants = invocation.getArgument(0);
            savedGrants.set(grants);
            return List.copyOf(grants);
        });

        var result = grantor.grantInitial(USER_ID);

        verify(registry).getAll();
        verify(store).saveAll(anyCollection());
        verifyNoInteractions(userReader);

        assertThat(savedGrants.get()).hasSize(1);
        var savedGrant = savedGrants.get().iterator().next();
        assertThat(savedGrant.getUserId()).isEqualTo(USER_ID);
        assertThat(savedGrant.getNamespaceRole().namespace().value()).isEqualTo(NAMESPACE_VALUE);
        assertThat(savedGrant.getNamespaceRole().role()).isEqualTo(ROLE);
        assertThat(savedGrant.getCreatedAt()).isEqualTo(CLOCK.instant());
        assertThat(result).containsExactly(savedGrant);
    }
}
