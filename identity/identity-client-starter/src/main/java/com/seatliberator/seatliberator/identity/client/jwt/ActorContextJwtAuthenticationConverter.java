package com.seatliberator.seatliberator.identity.client.jwt;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.Capability;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleCapabilitiesRegistry;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleDeserializer;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import com.seatliberator.seatliberator.kernel.CurrentApplicationNamespaceProvider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActorContextJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String SCOPES_CLAIM = "scopes";

    private final NamespaceRoleDeserializer deserializer;
    private final NamespaceRoleCapabilitiesRegistry registry;
    private final CurrentApplicationNamespaceProvider namespaceProvider;

    public ActorContextJwtAuthenticationConverter(
            NamespaceRoleDeserializer deserializer,
            NamespaceRoleCapabilitiesRegistry registry,
            CurrentApplicationNamespaceProvider namespaceProvider
    ) {
        this.deserializer = deserializer;
        this.registry = registry;
        this.namespaceProvider = namespaceProvider;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        String subject = Optional.ofNullable(source)
                .map(JwtClaimAccessor::getSubject)
                .orElseThrow(() -> new IllegalArgumentException("Missing jwt subject"));
        Set<String> scopes = readScopesClaim(source.getClaim(SCOPES_CLAIM));

        ApplicationNamespace currentNamespace = namespaceProvider.current();
        Set<NamespaceRole> currentNamespaceRoles = materializeNamespaceRoles(scopes).stream()
                .filter(namespaceRole ->  namespaceRole.namespace().isSame(currentNamespace))
                .collect(Collectors.toSet());

        Set<Role> currentRole = currentNamespaceRoles.stream()
                .map(NamespaceRole::role)
                .collect(Collectors.toUnmodifiableSet());
        Set<Capability> currentCapability = registry.resolve(currentNamespaceRoles);

        Actor actor = SimpleActor.of(subject, currentCapability);

        var authorities = convertToAuthorities(currentRole, currentCapability);

        return new ActorContextAuthenticationToken(
                actor,
                source,
                authorities
        );
    }

    private Set<GrantedAuthority> convertToAuthorities(Set<Role> roles, Set<Capability> capabilities) {
        var roleAuthorities = roles.stream()
                .map(role -> "ROLE_%s".formatted(role.name()));
        var capabilityAuthorities = capabilities.stream()
                .map(Capability::scope);

        return Stream.concat(roleAuthorities, capabilityAuthorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<NamespaceRole> materializeNamespaceRoles(Set<String> scopes) {
        return scopes.stream()
                .map(this::materializeNamespaceRole)
                .flatMap(Optional::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Optional<NamespaceRole> materializeNamespaceRole(String scope) {
        try {
            return Optional.of(deserializer.materialize(scope));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    private Set<String> readScopesClaim(Object claim) {
        if (claim instanceof String raw) {
            return Arrays.stream(raw.split("[,\\s]+"))
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .collect(Collectors.toSet());
        }

        if (claim instanceof Collection<?> values) {
            return values.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .collect(Collectors.toSet());
        }

        return Set.of();
    }
}
