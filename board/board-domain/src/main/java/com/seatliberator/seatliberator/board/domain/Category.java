package com.seatliberator.seatliberator.board.domain;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "board_id", nullable = false, updatable = false)
    private UUID boardId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false, insertable = false, updatable = false)
    private Board board;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private Category(UUID boardId, String name, String description, Instant createdAt) {
        this.boardId = Preconditions.requireNonNull(boardId, "boardId");
        this.name = Preconditions.requireNonBlank(name, "name");
        this.description = Preconditions.requireNonBlank(description, "description");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
    }

    public static Category of(UUID boardId, String name, String description, Instant createdAt) {
        return new Category(boardId, name, description, createdAt);
    }

    public void updateName(String name) {
        this.name = Preconditions.requireNonBlank(name, "name");
    }

    public void updateDescription(String description) {
        this.description = Preconditions.requireNonNull(description, "description");
    }
}
