package com.seatliberator.seatliberator.board.domain;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "post_id", nullable = false, updatable = false)
    private UUID postId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false, insertable = false, updatable = false)
    private Post post;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    private Comment(UUID postId, String userId, String content, Instant createdAt) {
        this.postId = Preconditions.requireNonNull(postId, "postId");
        this.userId = Preconditions.requireNonBlank(userId, "userId");
        this.content = Preconditions.requireNonBlank(content, "content");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
    }

    public static Comment of(UUID postId, String userId, String content, Instant createdAt) {
        return new Comment(postId, userId, content, createdAt);
    }

    public void updateContent(String content, Instant updatedAt) {
        this.updatedAt = evaluateUpdatedAt(updatedAt);
        this.content = Preconditions.requireNonBlank(content, "content");
    }

    private Instant evaluateUpdatedAt(Instant updatedAt) {
        Preconditions.requireNonNull(updatedAt, "updatedAt");

        if (updatedAt.isBefore(createdAt))
            throw new IllegalArgumentException("updatedAt must be after createdAt.");

        return updatedAt;
    }
}