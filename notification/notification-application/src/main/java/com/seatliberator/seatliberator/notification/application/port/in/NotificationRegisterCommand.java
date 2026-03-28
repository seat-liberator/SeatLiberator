package com.seatliberator.seatliberator.notification.application.port.in;

import com.seatliberator.seatliberator.notification.domain.NotificationLevel;

public record NotificationRegisterCommand(
        String targetUserId,
        NotificationLevel level,
        String title,
        String body
) {
}
