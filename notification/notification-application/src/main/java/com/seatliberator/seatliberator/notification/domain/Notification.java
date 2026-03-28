package com.seatliberator.seatliberator.notification.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "target_user_id", nullable = false)
    private String targetUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private NotificationLevel level;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "notified_at", nullable = false, updatable = false)
    private Instant notifiedAt;

    @Column(name = "read_at", nullable = true)
    private Instant readAt;

    public Notification(String targetUserId, NotificationLevel level, String title, String body, Instant notifiedAt, Instant readAt) {
        this.targetUserId = targetUserId;
        this.level = level;
        this.title = title;
        this.body = body;
        this.notifiedAt = notifiedAt;
        this.readAt = readAt;
    }

    public static Notification create(
            String targetUserId,
            NotificationLevel level,
            String title,
            String body,
            Instant notifiedAt
    ) {
        return new Notification(targetUserId, level, title, body, notifiedAt, null);
    }

    public boolean isRead() {
        return readAt != null;
    }
}
