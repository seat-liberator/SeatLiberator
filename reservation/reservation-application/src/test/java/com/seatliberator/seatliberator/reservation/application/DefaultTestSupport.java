package com.seatliberator.seatliberator.reservation.application;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.ActorFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.TestSupport;

import java.time.Clock;
import java.time.Instant;

public class DefaultTestSupport {
    public static final Clock CLOCK = TestSupport.fixedClock;
    public static final Instant NOW = CLOCK.instant();

    public static final String USER_ID = "user-1";
    public static final Actor ACTOR = new ActorFixture.Builder().subject(USER_ID).build();
}
