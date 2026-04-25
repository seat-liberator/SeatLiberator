package com.seatliberator.seatliberator.reservation.application.shared.notifier;

import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventPublisher;
import com.seatliberator.seatliberator.notification.api.event.NotificationCreateRequestEventPayload;
import com.seatliberator.seatliberator.notification.api.event.NotificationEventType;
import tools.jackson.databind.ObjectMapper;

public class Notifier {
    private final EventTraceHolder traceHolder;
    private final EventPublisher publisher;
    private final ObjectMapper mapper;

    public Notifier(
            EventTraceHolder traceHolder,
            EventPublisher publisher,
            ObjectMapper mapper
    ) {
        this.traceHolder = traceHolder;
        this.publisher = publisher;
        this.mapper = mapper;
    }

    public <T> void consume(String targetUserId, String level, String title, T payload) {
        var stringifiedPayload = mapper.writeValueAsString(payload);
        var envelope = new NotificationCreateRequestEventPayload(targetUserId, level, title, stringifiedPayload);

        traceHolder.with(
                () -> publisher.publish(NotificationEventType.NOTIFICATION_CREATE_REQUEST, envelope),
                state -> state.withAggregate("payload", "test")
        );
    }
}
