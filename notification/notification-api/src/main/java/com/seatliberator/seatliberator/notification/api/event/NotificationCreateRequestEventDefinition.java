package com.seatliberator.seatliberator.notification.api.event;

import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinition;
import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
public class NotificationCreateRequestEventDefinition implements EventDefinition<NotificationCreateRequestEventPayload> {
    @Override
    public @NonNull EventType type() {
        return NotificationEventType.NOTIFICATION_CREATE_REQUEST;
    }

    @Override
    public @NonNull Class<NotificationCreateRequestEventPayload> payloadType() {
        return NotificationCreateRequestEventPayload.class;
    }
}
