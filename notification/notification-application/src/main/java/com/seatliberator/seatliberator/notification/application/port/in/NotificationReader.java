package com.seatliberator.seatliberator.notification.application.port.in;

import java.util.List;

public interface NotificationReader {
    List<NotificationEntry> readByTargetUserId(String targetUserId);
}
