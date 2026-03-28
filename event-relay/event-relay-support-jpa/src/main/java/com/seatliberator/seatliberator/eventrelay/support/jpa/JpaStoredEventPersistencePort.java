package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.relay.EventFlow;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;

import java.time.Instant;
import java.util.List;

public interface JpaStoredEventPersistencePort {
    List<JpaStoredEvent> claim(List<EventStatus> statuses, EventFlow flow, int limit);

    int markProcessing(String id, Instant startedAt);

    int markResolved(String id, EventStatus status, Instant resolvedAt);

    List<JpaStoredEvent> findAllByIds(List<String> ids);
}
