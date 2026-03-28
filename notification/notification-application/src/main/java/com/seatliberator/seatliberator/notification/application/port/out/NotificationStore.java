package com.seatliberator.seatliberator.notification.application.port.out;

import com.seatliberator.seatliberator.notification.domain.Notification;

import java.util.List;

public interface NotificationStore {
    Notification save(Notification notification);

    List<Notification> findByTargetUserId(String targetUserId);
}
