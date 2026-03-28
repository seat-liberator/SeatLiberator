package com.seatliberator.seatliberator.notification.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByTargetUserId(String targetUserId);
}
