package com.seatliberator.seatliberator.identity.client.actor;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class SimpleActorFactoryTest {
    @Test
    @DisplayName("SimpleActorFactory는 SimpleActor를 생성한다")
    void shouldCreateSimpleActor() {
        // given
        ActorFactory factory = new SimpleActorFactory();
        var subject = "user-1";
        var scopes = Set.of("seat:read", "seat:reserve");

        // when
        Actor actor = factory.createActor(subject, scopes);

        // then
        assertNotNull(actor);
        assertInstanceOf(SimpleActor.class, actor);
        assertEquals(subject, actor.subject());
        assertEquals(scopes, actor.scopes());
    }
}
