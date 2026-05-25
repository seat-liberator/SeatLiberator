package com.seatliberator.seatliberator.identity.starter.security.jwt;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.Capability;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleCapabilitiesRegistry;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleDeserializer;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.kernel.CurrentApplicationNamespaceProvider;
import com.seatliberator.seatliberator.kernel.SimpleApplicationNamespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActorContextJwtAuthenticationConverter")
class ActorContextJwtAuthenticationConverterTest {

    @Mock
    private NamespaceRoleDeserializer deserializer;

    @Mock
    private NamespaceRoleCapabilitiesRegistry registry;

    @Mock
    private CurrentApplicationNamespaceProvider namespaceProvider;

    private ActorContextJwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ActorContextJwtAuthenticationConverter(deserializer, registry, namespaceProvider);
    }

    @Test
    @DisplayName("현재 namespace role과 resolve된 capability로 인증 토큰을 만든다")
    void convertsCurrentNamespaceRolesToActorAndAuthorities() {
        var reservation = SimpleApplicationNamespace.of("reservation");
        var role = SimpleNamespaceRole.from(reservation, Role.ADMIN);
        var capabilities = Set.of(
                capability("book:create"),
                capability("book:cancel")
        );

        given(namespaceProvider.current()).willReturn(reservation);
        given(deserializer.materialize("reservation:ADMIN")).willReturn(role);
        given(registry.resolve(Set.of(role))).willReturn(capabilities);

        var token = converter.convert(jwt("user-1", Map.of("scopes", List.of("reservation:ADMIN"))));

        assertThat(token).isInstanceOf(ActorContextAuthenticationToken.class);
        assertThat(token.getCredentials()).isInstanceOf(Jwt.class);
        assertThat(token.getAuthorities())
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("book:create"),
                        new SimpleGrantedAuthority("book:cancel")
                );

        var actor = (Actor) token.getPrincipal();
        assertThat(actor.subject()).isEqualTo("user-1");
        assertThat(actor.capabilities())
                .extracting(Capability::scope)
                .containsExactlyInAnyOrder("book:create", "book:cancel");
    }

    @Test
    @DisplayName("다른 namespace role은 role authority와 capability 확장 대상에서 제외한다")
    void ignoresNamespaceRolesOutsideCurrentNamespace() {
        var reservation = SimpleApplicationNamespace.of("reservation");
        var reservationUser = SimpleNamespaceRole.from(reservation, Role.USER);
        var boardAdmin = SimpleNamespaceRole.from(SimpleApplicationNamespace.of("board"), Role.ADMIN);
        var capability = capability("reservation:read");

        given(namespaceProvider.current()).willReturn(reservation);
        given(deserializer.materialize("reservation:USER")).willReturn(reservationUser);
        given(deserializer.materialize("board:ADMIN")).willReturn(boardAdmin);
        given(registry.resolve(Set.of(reservationUser))).willReturn(Set.of(capability));

        var token = converter.convert(jwt(
                "user-1",
                Map.of("scopes", List.of("reservation:USER", "board:ADMIN"))
        ));

        assertThat(token.getAuthorities())
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("reservation:read")
                );

        var actor = (Actor) token.getPrincipal();
        assertThat(actor.capabilities())
                .extracting(Capability::scope)
                .containsExactly("reservation:read");
    }

    @Test
    @DisplayName("scopes 문자열은 공백과 콤마 기준으로 읽어 scope 단위로 deserializer에 전달한다")
    void readsScopesStringClaim() {
        var reservation = SimpleApplicationNamespace.of("reservation");
        var role = SimpleNamespaceRole.from(reservation, Role.USER);
        var boardRole = SimpleNamespaceRole.from(SimpleApplicationNamespace.of("board"), Role.ADMIN);

        given(namespaceProvider.current()).willReturn(reservation);
        given(deserializer.materialize("reservation:USER")).willReturn(role);
        given(deserializer.materialize("board:ADMIN")).willReturn(boardRole);
        given(registry.resolve(Set.of(role))).willReturn(Set.of());

        converter.convert(jwt(
                "user-1",
                Map.of("scopes", "reservation:USER, reservation:USER   board:ADMIN")
        ));

        verify(deserializer).materialize("reservation:USER");
        verify(deserializer).materialize("board:ADMIN");
    }

    @Test
    @DisplayName("유효하지 않은 scope는 무시하고 유효한 scope만 인증 토큰에 반영한다")
    void ignoresInvalidScope() {
        var reservation = SimpleApplicationNamespace.of("reservation");
        var role = SimpleNamespaceRole.from(reservation, Role.USER);
        var capability = capability("reservation:read");

        given(namespaceProvider.current()).willReturn(reservation);
        given(deserializer.materialize("reservation:USER")).willReturn(role);
        given(deserializer.materialize("boom")).willThrow(new IllegalArgumentException("invalid namespace role"));
        given(registry.resolve(Set.of(role))).willReturn(Set.of(capability));

        var token = converter.convert(jwt(
                "user-1",
                Map.of("scopes", List.of("reservation:USER", "boom"))
        ));

        assertThat(token.getAuthorities())
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("reservation:read")
                );
    }

    @Test
    @DisplayName("subject가 없으면 예외를 던진다")
    void throwsWhenSubjectIsMissing() {
        var jwt = jwt(null, Map.of("scopes", List.of("reservation:USER")));

        assertThatThrownBy(() -> converter.convert(jwt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Missing jwt subject");
    }

    private Jwt jwt(String subject, Map<String, Object> claims) {
        return new Jwt(
                "token-value",
                Instant.parse("2026-04-05T00:00:00Z"),
                Instant.parse("2026-04-05T01:00:00Z"),
                Map.of("alg", "none"),
                withSubject(subject, claims)
        );
    }

    private Map<String, Object> withSubject(String subject, Map<String, Object> claims) {
        var map = new LinkedHashMap<String, Object>();
        map.putAll(claims);
        if (subject != null) {
            map.put("sub", subject);
        }
        return map;
    }

    private Capability capability(String scope) {
        return new TestCapability(scope);
    }

    private record TestCapability(String scope) implements Capability {
        @Override
        public String description() {
            return scope;
        }
    }
}
