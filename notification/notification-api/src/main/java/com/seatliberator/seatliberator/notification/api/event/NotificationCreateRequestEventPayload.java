package com.seatliberator.seatliberator.notification.api.event;

import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;

public record NotificationCreateRequestEventPayload(
        String targetUserId,
        String level,
        String title,
        String body
) implements EventPayload {
}
