package com.seatliberator.seatliberator.reservation.application;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.ActorFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.*;

import java.time.Clock;
import java.time.Instant;

public abstract class AbstractTestSupport {
    public static final Clock CLOCK = TestSupport.fixedClock;
    public static final Instant NOW = CLOCK.instant();

    public static final Instant START_AT = NOW;
    public static final Instant END_AT = NOW.plusSeconds(60);
    public static final InstantRange RANGE = SimpleInstantRange.of(START_AT, END_AT);

    public static final String ROOM_ID = "room-1";
    public static final String SEAT_ID = "seat-1";
    public static final SeatLocator LOCATOR = SimpleSeatLocator.of(ROOM_ID, SEAT_ID);

    public static final String USER_ID = "user-1";
    public static final Actor ACTOR = new ActorFixture.Builder().subject(USER_ID).build();
}
