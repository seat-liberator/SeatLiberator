package com.seatliberator.seatliberator.board.domain;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private Board(String name, String description, Instant createdAt) {
        this.name = Preconditions.requireNonBlank(name, "name");
        this.description = Preconditions.requireNonBlank(description, "description");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
    }

    public static Board of(String name, String description, Instant createdAt) {
        return new Board(name, description, createdAt);
    }

    public void updateName(String name) {
        this.name = Preconditions.requireNonBlank(name, "name");
    }

    public void updateDescription(String description) {
        this.description = Preconditions.requireNonBlank(description, "description");
    }
}
