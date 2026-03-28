package com.seatliberator.seatliberator.notification.infrastructure.event;

import com.seatliberator.seatliberator.eventrelay.core.codec.EventPayloadDeserializer;
import com.seatliberator.seatliberator.eventrelay.core.factory.ThreadLocalEventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.relay.inbound.EventListenerRegistry;
import com.seatliberator.seatliberator.eventrelay.core.relay.inbound.RegistryEventRouter;
import com.seatliberator.seatliberator.notification.api.event.NotificationCreateRequestEventDefinition;
import com.seatliberator.seatliberator.notification.api.event.NotificationCreateRequestEventPayload;
import com.seatliberator.seatliberator.notification.api.event.NotificationEventType;
import com.seatliberator.seatliberator.notification.application.port.out.NotificationStore;
import com.seatliberator.seatliberator.notification.application.service.NotificationService;
import com.seatliberator.seatliberator.notification.domain.Notification;
import com.seatliberator.seatliberator.notification.domain.NotificationLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class NotificationEventRelayFlowTest {

    @Test
    @DisplayName("notification create 이벤트를 route하면 알림이 저장된다")
    void save_notification_when_routing_notification_create_event() {
        var store = new InMemoryNotificationStore();
        var clock = Clock.fixed(Instant.parse("2026-03-29T00:00:00Z"), ZoneOffset.UTC);
        var service = new NotificationService(store, clock);
        var definition = new NotificationCreateRequestEventDefinition();
        var listener = new NotificationEventListener(service, definition);

        var payload = new NotificationCreateRequestEventPayload(
                "user-1",
                NotificationLevel.WARNING.name(),
                "좌석 알림",
                "원하는 시간대의 좌석이 비었습니다."
        );
        var envelope = mock(EventEnvelope.class, RETURNS_DEEP_STUBS);
        var listenerRegistry = mock(EventListenerRegistry.class);
        var payloadDeserializer = mock(EventPayloadDeserializer.class);

        given(envelope.header().eventType()).willReturn(NotificationEventType.NOTIFICATION_CREATE_REQUEST);
        given(envelope.rawPayload()).willReturn("{\"ignored\":true}");
        doReturn(listener).when(listenerRegistry).resolve(NotificationEventType.NOTIFICATION_CREATE_REQUEST);
        given(payloadDeserializer.materialize(
                "{\"ignored\":true}",
                NotificationCreateRequestEventPayload.class
        )).willReturn(payload);

        var router = new RegistryEventRouter(listenerRegistry, payloadDeserializer, new ThreadLocalEventTraceHolder());

        router.route(envelope);

        assertThat(store.notifications).hasSize(1);
        assertThat(store.notifications.getFirst().getTargetUserId()).isEqualTo("user-1");
        assertThat(store.notifications.getFirst().getLevel()).isEqualTo(NotificationLevel.WARNING);
        assertThat(store.notifications.getFirst().getTitle()).isEqualTo("좌석 알림");
        assertThat(store.notifications.getFirst().getBody()).isEqualTo("원하는 시간대의 좌석이 비었습니다.");
        assertThat(store.notifications.getFirst().getNotifiedAt()).isEqualTo(clock.instant());
    }

    private static class InMemoryNotificationStore implements NotificationStore {
        private final List<Notification> notifications = new ArrayList<>();

        @Override
        public Notification save(Notification notification) {
            notifications.add(notification);
            return notification;
        }

        @Override
        public List<Notification> findByTargetUserId(String targetUserId) {
            return notifications.stream()
                    .filter(notification -> notification.getTargetUserId().equals(targetUserId))
                    .toList();
        }
    }
}
