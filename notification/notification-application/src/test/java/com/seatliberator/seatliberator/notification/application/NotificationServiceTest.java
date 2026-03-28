package com.seatliberator.seatliberator.notification.application;

import com.seatliberator.seatliberator.notification.application.port.in.NotificationRegisterCommand;
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

public class NotificationServiceTest {

    @Test
    @DisplayName("알림 등록 시 store에 저장하고 조회할 수 있다")
    void save_and_read_notification_when_registering_notification() {
        var store = new InMemoryNotificationStore();
        var clock = Clock.fixed(Instant.parse("2026-03-29T00:00:00Z"), ZoneOffset.UTC);
        var service = new NotificationService(store, clock);

        var command = new NotificationRegisterCommand(
                "user-1",
                NotificationLevel.INFO,
                "예약 알림",
                "빈 자리가 생겼습니다."
        );

        var saved = service.register(command);
        var found = service.readByTargetUserId("user-1");

        assertThat(store.notifications).hasSize(1);
        assertThat(store.notifications.getFirst().getTargetUserId()).isEqualTo("user-1");
        assertThat(store.notifications.getFirst().getLevel()).isEqualTo(NotificationLevel.INFO);
        assertThat(store.notifications.getFirst().getTitle()).isEqualTo("예약 알림");
        assertThat(store.notifications.getFirst().getBody()).isEqualTo("빈 자리가 생겼습니다.");
        assertThat(store.notifications.getFirst().getNotifiedAt()).isEqualTo(clock.instant());

        assertThat(saved.targetUserId()).isEqualTo("user-1");
        assertThat(saved.level()).isEqualTo(NotificationLevel.INFO);
        assertThat(saved.title()).isEqualTo("예약 알림");
        assertThat(saved.content()).isEqualTo("빈 자리가 생겼습니다.");

        assertThat(found).hasSize(1);
        assertThat(found.getFirst().targetUserId()).isEqualTo("user-1");
        assertThat(found.getFirst().title()).isEqualTo("예약 알림");
        assertThat(found.getFirst().content()).isEqualTo("빈 자리가 생겼습니다.");
        assertThat(found.getFirst().read()).isFalse();
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
