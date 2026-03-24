package com.seatliberator.seatliberator.eventrelay.core.store.model;

import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import org.jspecify.annotations.NonNull;

public interface EventFlowAware {
    @NonNull EventFlow flow();

    default boolean isInbound() {
        return flow() == EventFlow.INBOUND;
    }

    default boolean isOutbound() {
        return flow() == EventFlow.OUTBOUND;
    }
}
