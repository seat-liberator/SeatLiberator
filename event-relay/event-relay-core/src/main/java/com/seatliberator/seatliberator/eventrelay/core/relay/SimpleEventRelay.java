package com.seatliberator.seatliberator.eventrelay.core.relay;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.inbound.EventRouter;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventSender;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import org.jspecify.annotations.NonNull;

import java.time.Clock;
import java.util.function.Consumer;

public class SimpleEventRelay implements EventRelay {
    private final Clock clock;
    private final EventStore store;
    private final EventRouter router;
    private final EventSender sender;

    public SimpleEventRelay(
            @NonNull Clock clock,
            @NonNull EventStore store,
            @NonNull EventRouter router,
            @NonNull EventSender sender
    ) {
        this.clock = clock;
        this.store = store;
        this.router = router;
        this.sender = sender;
    }

    @Override
    public void run() {
        runSingleFlow(EventFlow.OUTBOUND, sender::send);
        runSingleFlow(EventFlow.INBOUND, router::route);
    }

    private void runSingleFlow(EventFlow flow, Consumer<EventEnvelope> action) {
        var envelopes = store.claimBatch(flow, clock.instant());

        for (var e : envelopes) {
            var eventId = e.trace().eventId();
            try {
                action.accept(e);
                store.reportCompleted(eventId, clock.instant());
            } catch (Exception ex) {
                store.reportFailed(eventId, clock.instant());
            }
        }
    }
}
