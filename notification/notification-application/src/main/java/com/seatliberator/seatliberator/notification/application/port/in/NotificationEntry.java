package com.seatliberator.seatliberator.notification.application.port.in;

import com.seatliberator.seatliberator.notification.domain.Notification;
import com.seatliberator.seatliberator.notification.domain.NotificationLevel;

import java.time.Instant;
import java.util.UUID;

public record NotificationEntry(
        UUID id,
        String targetUserId,
        NotificationLevel level,
        String title,
        String content,
        Instant notifiedAt,
        boolean read
) {
    public static NotificationEntry from(Notification notification) {
        return new NotificationEntry(
                notification.getId(),
                notification.getTargetUserId(),
                notification.getLevel(),
                notification.getTitle(),
                notification.getBody(),
                notification.getNotifiedAt(),
                notification.isRead()
        );
    }
}
