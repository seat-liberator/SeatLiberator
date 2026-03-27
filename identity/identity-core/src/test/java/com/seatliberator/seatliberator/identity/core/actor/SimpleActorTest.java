package com.seatliberator.seatliberator.identity.core.actor;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleActorTest {
    @Test
    @DisplayName("SimpleActor는 subject와 scopes를 그대로 반환한다")
    void SimpleActor는_subject와_scopes를_그대로_반환한다() {
        // given
        var subject = "user-1";
        var scopes = Set.of("seat:read", "seat:reserve");

        // when
        Actor actor = new SimpleActor(subject, scopes);

        // then
        assertEquals(subject, actor.subject());
        assertEquals(scopes, actor.scopes());
    }
}
