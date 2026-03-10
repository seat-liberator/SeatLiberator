package com.seatliberator.seatliberator.identity.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "credential_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CredentialAccount extends AbstractAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    public CredentialAccount(
            String email,
            String passwordHash
    ) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public static CredentialAccount create(
            String email,
            String passwordHash
    ) {
        return new CredentialAccount(email, passwordHash);
    }

    @Override
    public void assignUser(User user) {
        this.user = user;
    }
}
