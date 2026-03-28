package com.seatliberator.seatliberator.notification.application.service;

import com.seatliberator.seatliberator.notification.application.port.in.NotificationEntry;
import com.seatliberator.seatliberator.notification.application.port.in.NotificationReader;
import com.seatliberator.seatliberator.notification.application.port.in.NotificationRegisterCommand;
import com.seatliberator.seatliberator.notification.application.port.in.NotificationRegistrar;
import com.seatliberator.seatliberator.notification.application.port.out.NotificationStore;
import com.seatliberator.seatliberator.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService implements NotificationRegistrar, NotificationReader {
    private final NotificationStore notificationStore;
    private final Clock clock;

    @Override
    public NotificationEntry register(NotificationRegisterCommand command) {
        var notification = Notification.create(
                command.targetUserId(),
                command.level(),
                command.title(),
                command.body(),
                clock.instant()
        );
        var saved = notificationStore.save(notification);
        return NotificationEntry.from(saved);
    }

    @Override
    public List<NotificationEntry> readByTargetUserId(String targetUserId) {
        return notificationStore.findByTargetUserId(targetUserId).stream()
                .map(NotificationEntry::from)
                .toList();
    }
}
