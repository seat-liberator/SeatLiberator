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
        name = "federated_account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_federated_account_registration_provider_user",
                        columnNames = {"registration_id", "provider_user_id"}
                ),
                @UniqueConstraint(
                        name = "uk_federated_account_user_id_registration_id",
                        columnNames = {"user_id", "registration_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FederatedAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @Column(name = "registration_id", nullable = false)
    private String registrationId;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public FederatedAccount(UUID userId, String registrationId, String providerUserId, Instant createdAt) {
        this.userId = Preconditions.requireNonNull(userId, "userId");
        this.registrationId = Preconditions.requireNonBlank(registrationId, "registrationId");
        this.providerUserId = Preconditions.requireNonBlank(providerUserId, "providerUserId");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
    }

    public static FederatedAccount of(UUID userId, String registrationId, String providerUserId, Instant createdAt) {
        return new FederatedAccount(userId, registrationId, providerUserId, createdAt);
    }
}
