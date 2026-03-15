package com.seatliberator.seatliberator.identity.client.validate.jwt;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Set;

public class ActorContextJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        String subject = source.getSubject();

        Actor actor = new SimpleActor(
                subject,
                Set.of()
        );

        return new ActorContextAuthenticationToken(
                actor,
                source,
                List.of()
        );
    }
}
