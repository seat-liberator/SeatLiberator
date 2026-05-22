package com.seatliberator.seatliberator.identity.server.domain.account;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public User(String nickname, Instant createdAt) {
        this.nickname = Preconditions.requireNonBlank(nickname, "nickname");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
    }

    public static User of(String nickname, Instant createdAt) {
        return new User(nickname, createdAt);
    }

    public void updateNickname(String nickname, Instant updatedAt) {
        this.updatedAt = evaluateUpdatedAt(updatedAt);
        this.nickname = Preconditions.requireNonBlank(nickname, "nickname");
    }

    private Instant evaluateUpdatedAt(Instant updatedAt) {
        Preconditions.requireNonNull(updatedAt, "updatedAt");

        if (updatedAt.isBefore(createdAt))
            throw new IllegalArgumentException("updatedAt must not be before createdAt");

        return updatedAt;
    }
}
