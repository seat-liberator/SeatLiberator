package com.seatliberator.seatliberator.bootstrap.security;

import com.seatliberator.seatliberator.identity.client.role.Capability;
import com.seatliberator.seatliberator.identity.client.role.NamespaceRoleCapabilitiesRegistry;
import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleDeserializer;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.kernel.CurrentApplicationNamespaceProvider;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;

import java.util.*;
import java.util.stream.Collectors;

public class ActorContextJwtAuthenticationConverter implements JwtAuthenticationTokenConverter {
    private static final String SCOPE_CLAIM = "scope";
    private static final String SCOPES_CLAIM = "scopes";
    private final NamespaceRoleDeserializer deserializer;
    private final NamespaceRoleCapabilitiesRegistry registry;
    private final CurrentApplicationNamespaceProvider currentApplicationNamespaceProvider;

    public ActorContextJwtAuthenticationConverter(
            NamespaceRoleDeserializer deserializer,
            NamespaceRoleCapabilitiesRegistry registry,
            CurrentApplicationNamespaceProvider currentApplicationNamespaceProvider
    ) {
        this.deserializer = deserializer;
        this.registry = registry;
        this.currentApplicationNamespaceProvider = currentApplicationNamespaceProvider;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        String subject = Optional.ofNullable(source)
                .map(JwtClaimAccessor::getSubject)
                .orElseThrow(() -> new IllegalArgumentException("Missing jwt subject"));
        Set<String> scopes = extractScopes(source);
        var authorities = scopes.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        Actor actor = new SimpleActor(
                subject,
                scopes
        );

        return new ActorContextAuthenticationToken(
                actor,
                source,
                authorities
        );
    }

    private Set<String> extractScopes(Jwt source) {
        var scopes = new LinkedHashSet<String>();
        scopes.addAll(readScopesClaim(source.getClaim(SCOPES_CLAIM)));
        scopes.addAll(readScopesClaim(source.getClaim(SCOPE_CLAIM)));

        var namespaceRoles = scopes.stream()
                .map(deserializer::tryMaterialize)
                .flatMap(Optional::stream)
                .filter(r -> r.namespace().value().equals(currentApplicationNamespaceProvider.current().value()))
                .collect(Collectors.toUnmodifiableSet());

        var roles = namespaceRoles.stream()
                .map(NamespaceRole::role)
                .map(Role::name)
                .map(roleName -> "ROLE_" + roleName)
                .collect(Collectors.toUnmodifiableSet());

        var capabilities = namespaceRoles.stream()
                .map(registry::resolve)
                .flatMap(Collection::stream)
                .map(Capability::scope)
                .collect(Collectors.toUnmodifiableSet());

        scopes.addAll(roles);
        scopes.addAll(capabilities);

        return Set.copyOf(scopes);
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
