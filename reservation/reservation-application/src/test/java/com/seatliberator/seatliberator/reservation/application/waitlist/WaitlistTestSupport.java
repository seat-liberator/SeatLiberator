package com.seatliberator.seatliberator.reservation.application.waitlist;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.ActorFixture;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.reservation.application.DefaultTestSupport;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistFixture;

import java.util.Set;
import java.util.UUID;

public class WaitlistTestSupport extends DefaultTestSupport {
    public static final UUID WAITLIST_ID = UuidGenerator.generate(1);
    public static final String OTHER_USER_ID = "user-2";
    public static final Actor OTHER_ACTOR = new ActorFixture.Builder().subject(OTHER_USER_ID).build();
    public static final Actor WAITLIST_CREATOR = new ActorFixture.Builder()
            .subject(USER_ID)
            .capabilities(Set.of(ReservationCapability.WAITLIST_CREATE))
            .build();
    public static final Actor WAITLIST_CANCELLER = new ActorFixture.Builder()
            .subject(USER_ID)
            .capabilities(Set.of(ReservationCapability.WAITLIST_CANCEL))
            .build();
    public static final Actor WAITLIST_MANAGER = new ActorFixture.Builder()
            .subject(OTHER_USER_ID)
            .capabilities(Set.of(ReservationCapability.WAITLIST_MANAGE))
            .build();

    public static Waitlist waitlist() {
        return WaitlistFixture.createWaitlist();
    }
}
