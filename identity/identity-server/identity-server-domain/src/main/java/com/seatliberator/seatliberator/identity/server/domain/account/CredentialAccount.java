package com.seatliberator.seatliberator.identity.server.domain.account;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "credential_account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_credential_account_email",
                        columnNames = {"email"}
                ),
                @UniqueConstraint(
                        name = "uk_credential_account_user_id",
                        columnNames = {"user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CredentialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public CredentialAccount(UUID userId, String email, String passwordHash, Instant createdAt) {
        this.userId = Preconditions.requireNonNull(userId, "userId");
        this.email = Preconditions.requireNonBlank(email, "email");
        this.passwordHash = Preconditions.requireNonBlank(passwordHash, "passwordHash");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
    }

    public static CredentialAccount of(UUID userId, String email, String passwordHash, Instant createdAt) {
        return new CredentialAccount(userId, email, passwordHash, createdAt);
    }

    public void updatePasswordHash(String passwordHash, Instant updatedAt) {
        this.updatedAt = evaluateUpdatedAt(updatedAt);
        this.passwordHash = Preconditions.requireNonBlank(passwordHash, "passwordHash");
    }

    private Instant evaluateUpdatedAt(Instant updatedAt) {
        Preconditions.requireNonNull(updatedAt, "updatedAt");

        if (updatedAt.isBefore(createdAt))
            throw new IllegalArgumentException("updatedAt must not be before createdAt.");

        return updatedAt;
    }
}
