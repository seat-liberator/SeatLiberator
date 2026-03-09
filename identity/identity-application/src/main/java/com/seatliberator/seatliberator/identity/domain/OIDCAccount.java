package com.seatliberator.seatliberator.identity.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OIDCAccount extends AbstractAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "oidc_provider", nullable = false)
    private OIDCProvider oidcProvider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    public OIDCAccount(OIDCProvider oidcProvider, String providerId) {
        this.oidcProvider = oidcProvider;
        this.providerId = providerId;
    }

    public static OIDCAccount create(OIDCProvider oidcProvider, String providerId) {
        return new OIDCAccount(oidcProvider, providerId);
    }

    @Override
    public void assignUser(User user) {
        this.user = user;
    }
}
