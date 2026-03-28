package com.seatliberator.seatliberator.notification.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.notification.application.port.out.NotificationStore;
import com.seatliberator.seatliberator.notification.domain.Notification;
import com.seatliberator.seatliberator.notification.infrastructure.persistence.jpa.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaNotificationStore implements NotificationStore {
    private final NotificationRepository repository;

    @Override
    public Notification save(Notification notification) {
        return repository.save(notification);
    }

    @Override
    public List<Notification> findByTargetUserId(String targetUserId) {
        return repository.findByTargetUserId(targetUserId);
    }
}
