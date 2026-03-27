package com.seatliberator.seatliberator.eventrelay.core.scheduler;

import com.seatliberator.seatliberator.eventrelay.core.relay.EventRelay;
import org.jspecify.annotations.NonNull;
import org.springframework.scheduling.annotation.Scheduled;

public class FixedDelayEventScheduler implements EventScheduler {
    private final EventRelay relay;

    public FixedDelayEventScheduler(
            @NonNull EventRelay relay
    ) {
        this.relay = relay;
    }

    @Scheduled(fixedDelayString = "${event-relay.scheduler.fixed.delayMs:500}")
    public void tick() {
        relay.run();
    }
}

