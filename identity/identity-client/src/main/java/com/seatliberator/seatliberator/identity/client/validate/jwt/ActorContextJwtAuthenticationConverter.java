package com.seatliberator.seatliberator.identity.client.validate.jwt;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ActorContextJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String SCOPE_CLAIM = "scope";
    private static final String SCOPES_CLAIM = "scopes";

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        String subject = source.getSubject();
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

    private static Set<String> extractScopes(Jwt source) {
        var scopes = new LinkedHashSet<String>();
        scopes.addAll(readScopesClaim(source.getClaim(SCOPES_CLAIM)));
        scopes.addAll(readScopesClaim(source.getClaim(SCOPE_CLAIM)));
        return Set.copyOf(scopes);
    }

    private static Set<String> readScopesClaim(Object claim) {
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
