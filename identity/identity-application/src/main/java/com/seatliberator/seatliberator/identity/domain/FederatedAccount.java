package com.seatliberator.seatliberator.identity.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "federated_account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_federated_account_registration_provider_user",
                        columnNames = {"registration_id", "provider_user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FederatedAccount extends AbstractAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "registration_id", nullable = false)
    private String registrationId;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    public FederatedAccount(
            String registrationId,
            String providerUserId
    ) {
        this.registrationId = registrationId;
        this.providerUserId = providerUserId;
    }

    public static FederatedAccount create(
            String registrationId,
            String providerUserId
    ) {
        return new FederatedAccount(
                registrationId,
                providerUserId
        );
    }

    @Override
    public void assignUser(User user) {
        this.user = user;
    }
}
