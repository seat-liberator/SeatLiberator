package com.seatliberator.seatliberator.board.domain;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "board_id", nullable = false, updatable = false)
    private UUID boardId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false, insertable = false, updatable = false)
    private Board board;

    @Column(name = "category_id", nullable = false, updatable = false)
    private UUID categoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, insertable = false, updatable = false)
    private Category category;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    private Post(UUID boardId, UUID categoryId, String userId, String title, String content, Instant createdAt) {
        this.boardId = Preconditions.requireNonNull(boardId, "boardId");
        this.categoryId = Preconditions.requireNonNull(categoryId, "categoryId");
        this.userId = Preconditions.requireNonBlank(userId, "userId");
        this.title = Preconditions.requireNonBlank(title, "title");
        this.content = Preconditions.requireNonBlank(content, "content");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
    }

    public static Post of(UUID boardId, UUID categoryId, String userId, String title, String content, Instant createdAt) {
        return new Post(boardId, categoryId, userId, title, content, createdAt);
    }

    public void updateTitle(String title, Instant updatedAt) {
        this.updatedAt = evaluateUpdatedAt(updatedAt);
        this.title = Preconditions.requireNonBlank(title, "title");
    }

    public void updateContent(String content, Instant updatedAt) {
        this.updatedAt = evaluateUpdatedAt(updatedAt);
        this.content = Preconditions.requireNonBlank(content, "content");
    }

    public void updateCategoryId(UUID categoryId, Instant updatedAt) {
        this.updatedAt = evaluateUpdatedAt(updatedAt);
        this.categoryId = Preconditions.requireNonNull(categoryId, "categoryId");
    }

    public Instant evaluateUpdatedAt(Instant updatedAt) {
        Preconditions.requireNonNull(updatedAt, "updatedAt");

        if (updatedAt.isBefore(createdAt))
            throw new IllegalArgumentException("updatedAt must be after createdAt.");

        return updatedAt;
    }
}
