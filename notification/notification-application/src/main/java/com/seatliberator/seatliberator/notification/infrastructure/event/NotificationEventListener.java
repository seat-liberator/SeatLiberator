package com.seatliberator.seatliberator.notification.infrastructure.event;

import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinition;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.inbound.EventListener;
import com.seatliberator.seatliberator.notification.api.event.NotificationCreateRequestEventDefinition;
import com.seatliberator.seatliberator.notification.api.event.NotificationCreateRequestEventPayload;
import com.seatliberator.seatliberator.notification.application.port.in.NotificationRegisterCommand;
import com.seatliberator.seatliberator.notification.application.port.in.NotificationRegistrar;
import com.seatliberator.seatliberator.notification.domain.NotificationLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener implements EventListener<NotificationCreateRequestEventPayload> {
    private final NotificationRegistrar notificationRegistrar;
    private final NotificationCreateRequestEventDefinition definition;

    @Override
    public @NonNull EventDefinition<NotificationCreateRequestEventPayload> definition() {
        return definition;
    }

    @Override
    public void handle(@NonNull EventEnvelope envelope, @NonNull NotificationCreateRequestEventPayload payload) {
        NotificationLevel level;
        try {
            level = NotificationLevel.valueOf(payload.level());
        } catch (IllegalArgumentException e) {
            return;
        }
        var command = new NotificationRegisterCommand(
                payload.targetUserId(),
                level,
                payload.title(),
                payload.body()
        );
        log.debug("handle notification event listener command={}", command);
        notificationRegistrar.register(command);
    }
}
