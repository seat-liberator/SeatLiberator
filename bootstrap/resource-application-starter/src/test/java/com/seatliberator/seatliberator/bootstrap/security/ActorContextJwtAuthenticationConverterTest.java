package com.seatliberator.seatliberator.bootstrap.security;

import com.seatliberator.seatliberator.identity.client.jwt.ActorContextJwtAuthenticationConverter;
import com.seatliberator.seatliberator.identity.client.role.NamespaceRoleCapabilitiesRegistry;
import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.role.Capability;
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
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("Actor Context Jwt Authentication Converter")
public class ActorContextJwtAuthenticationConverterTest {

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
    @DisplayName("subject와 scope/scopes 기반으로 ActorContextAuthenticationToken을 생성한다")
    void convert_token_from_jwt() {
        var jwt = jwt(
                "user-1",
                Map.of(
                        "scope", "reservation:USER",
                        "scopes", List.of("reservation:USER")
                )
        );

        given(deserializer.tryMaterialize("reservation:USER"))
                .willReturn(Optional.empty());

        AbstractAuthenticationToken token = converter.convert(jwt);

        assertThat(token).isInstanceOf(com.seatliberator.seatliberator.identity.client.jwt.ActorContextAuthenticationToken.class);
        assertThat(token.getPrincipal()).isInstanceOf(Actor.class);
        assertThat(token.getCredentials()).isSameAs(jwt);

        Actor actor = (Actor) token.getPrincipal();

        assertThat(actor.subject()).isEqualTo("user-1");
        assertThat(actor.scopes())
                .containsExactlyInAnyOrder(
                        "reservation:USER"
                );
        assertThat(token.getAuthorities())
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority("reservation:USER")
                );
    }

    @Test
    @DisplayName("서비스 namespace와 일치하는 namespace role이 있으면 prefixed Role도 담는다")
    void namespace_role_create_spring_security_prefixed_role() {
        Jwt jwt = jwt("user-1", Map.of("scope", "reservation:USER"));

        var namespace = SimpleApplicationNamespace.of("reservation");

        given(deserializer.tryMaterialize("reservation:USER"))
                .willReturn(Optional.of(SimpleNamespaceRole.from(namespace, Role.USER)));
        given(namespaceProvider.current())
                .willReturn(namespace);

        var token = converter.convert(jwt);

        assertThat(token.getAuthorities())
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority("reservation:USER"),
                        new SimpleGrantedAuthority("ROLE_USER")
                );

        var actor = (Actor) token.getPrincipal();

        assertThat(actor.scopes())
                .containsExactlyInAnyOrder(
                        "reservation:USER",
                        "ROLE_USER"
                );
    }

    @Test
    @DisplayName("같은 namespace에 여러 Role이 있으면 여러 prefixed Role을 전부 담는다")
    void multiple_namespace_role_create_multiple_spring_security_prefixed_role() {
        Jwt jwt = jwt("user-1", Map.of("scope", List.of("reservation:USER", "reservation:ADMIN")));

        var namespace = SimpleApplicationNamespace.of("reservation");

        given(deserializer.tryMaterialize("reservation:USER"))
                .willReturn(Optional.of(SimpleNamespaceRole.from(namespace, Role.USER)));
        given(deserializer.tryMaterialize("reservation:ADMIN"))
                .willReturn(Optional.of(SimpleNamespaceRole.from(namespace, Role.ADMIN)));
        given(namespaceProvider.current())
                .willReturn(namespace);

        var token = converter.convert(jwt);

        assertThat(token.getAuthorities())
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority("reservation:USER"),
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("reservation:ADMIN"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                );

        var actor = (Actor) token.getPrincipal();

        assertThat(actor.scopes())
                .containsExactlyInAnyOrder(
                        "reservation:USER",
                        "ROLE_USER",
                        "reservation:ADMIN",
                        "ROLE_ADMIN"
                );
    }

    @Test
    @DisplayName("subject가 없으면 예외")
    void throw_exception_when_subject_missing() {
        var jwt = jwt(null, Map.of("scope", "reservation:USER"));

        assertThatThrownBy(() -> converter.convert(jwt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Missing jwt subject");
    }

    @Test
    @DisplayName("서비스 namespace와 다른 namespace role은 prefixed Role을 만들지 않는다")
    void do_not_create_prefixed_role_when_namespace_is_different() {
        var jwt = jwt("user-1", Map.of("scope", "board:USER"));

        var boardNamespace = SimpleApplicationNamespace.of("board");
        var reservationNamespace = SimpleApplicationNamespace.of("reservation");

        given(deserializer.tryMaterialize("board:USER"))
                .willReturn(Optional.of(SimpleNamespaceRole.from(boardNamespace, Role.USER)));
        given(namespaceProvider.current()).willReturn(reservationNamespace);

        var token = converter.convert(jwt);

        assertThat(token.getAuthorities())
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority("board:USER")
                );

        var actor = (Actor) token.getPrincipal();

        assertThat(actor.scopes())
                .containsExactlyInAnyOrder(
                        "board:USER"
                );
    }

    @Test
    @DisplayName("namespace role에 연결된 capability가 있으면 capability scope도 만든다")
    void namespace_role_add_capability_scopes() {
        var jwt = jwt("user-1", Map.of("scope", "reservation:ADMIN"));

        var namespace = SimpleApplicationNamespace.of("reservation");
        var role = SimpleNamespaceRole.from(namespace, Role.ADMIN);

        var createBookCapability = capability("book:create");
        var cancelBookCapability = capability("book:cancel");

        given(deserializer.tryMaterialize("reservation:ADMIN"))
                .willReturn(Optional.of(role));
        given(namespaceProvider.current())
                .willReturn(namespace);
        given(registry.resolve(role))
                .willReturn(Set.of(createBookCapability, cancelBookCapability));

        var token = converter.convert(jwt);

        assertThat(token.getAuthorities())
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority("reservation:ADMIN"),
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("book:create"),
                        new SimpleGrantedAuthority("book:cancel")
                );

        var actor = (Actor) token.getPrincipal();

        assertThat(actor.scopes())
                .containsExactlyInAnyOrder(
                        "reservation:ADMIN",
                        "ROLE_ADMIN",
                        "book:create",
                        "book:cancel"
                );
    }

    @Test
    @DisplayName("scope 문자열은 공백과 콤마를 구분자로 파싱할 수 있고 중복은 제거한다")
    void parse_scope_string_and_dedup() {
        var jwt = jwt("user-1", Map.of(
                "scope", "reservation:USER, reservation:USER   reservation:USER, board:ADMIN",
                "scopes", List.of("reservation:USER", "board:ADMIN", "reservation:USER")
        ));

        given(deserializer.tryMaterialize("reservation:USER")).willReturn(Optional.empty());
        given(deserializer.tryMaterialize("board:ADMIN")).willReturn(Optional.empty());

        var token = converter.convert(jwt);

        assertThat(token.getAuthorities())
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority("reservation:USER"),
                        new SimpleGrantedAuthority("board:ADMIN")
                );

        var actor = (Actor) token.getPrincipal();

        assertThat(actor.scopes())
                .containsExactlyInAnyOrder(
                        "reservation:USER",
                        "board:ADMIN"
                );
    }

    @Test
    @DisplayName("지원하지 않는 scope는 무시된다")
    void ignore_unsupported_scope_claim() {
        var jwt = jwt("user-1", Map.of("scope", 123, "scopes", Map.of("has", "reservation:USER")));

        var token = converter.convert(jwt);

        assertThat(token.getAuthorities()).isEmpty();

        var actor = (Actor) token.getPrincipal();

        assertThat(actor.scopes()).isEmpty();
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
        Capability capability = mock(Capability.class);
        given(capability.scope()).willReturn(scope);
        return capability;
    }
}
