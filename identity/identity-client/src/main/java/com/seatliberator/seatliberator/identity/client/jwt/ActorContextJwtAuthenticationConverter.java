package com.seatliberator.seatliberator.identity.client.jwt;

import com.seatliberator.seatliberator.identity.client.role.Capability;
import com.seatliberator.seatliberator.identity.client.role.NamespaceRoleCapabilitiesRegistry;
import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRoleDeserializer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;

import java.util.*;
import java.util.stream.Collectors;

public class ActorContextJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String SCOPE_CLAIM = "scope";
    private static final String SCOPES_CLAIM = "scopes";
    private final NamespaceRoleDeserializer deserializer;
    private final NamespaceRoleCapabilitiesRegistry registry;

    public ActorContextJwtAuthenticationConverter(
            NamespaceRoleDeserializer deserializer,
            NamespaceRoleCapabilitiesRegistry registry
    ) {
        this.deserializer = deserializer;
        this.registry = registry;
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

        var capabilities = scopes.stream()
                .map(deserializer::tryMaterialize)
                .flatMap(Optional::stream)
                .map(registry::resolve)
                .flatMap(Collection::stream)
                .map(Capability::scope)
                .collect(Collectors.toUnmodifiableSet());

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
