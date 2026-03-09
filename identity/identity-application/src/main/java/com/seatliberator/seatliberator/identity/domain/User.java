package com.seatliberator.seatliberator.identity.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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

    private String nickname;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.ALL
    )
    private List<OIDCAccount> oidcAccounts = new ArrayList<>();

    @OneToOne(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.ALL
    )
    private CredentialAccount credentialAccount;

    public User(
            String nickname,
            List<OIDCAccount> oidcAccounts,
            CredentialAccount credentialAccount
    ) {
        List<OIDCAccount> alignOIDCAccounts = oidcAccounts.stream()
                .peek(account -> {
                    if (account.getUser() != null && account.getUser() != this) {
                        account.assignUser(this);
                    }
                })
                .toList();

        if (credentialAccount.getUser() != null && credentialAccount.getUser() != this) {
            credentialAccount.assignUser(this);
        }

        this.nickname = nickname;
        this.oidcAccounts = alignOIDCAccounts;
        this.credentialAccount = credentialAccount;
    }

    public static User create(
            String nickname
    ) {
        var u = new User();

        u.nickname = nickname;

        return u;
    }

    public void addOidcAccount(OIDCAccount oidcAccount) {
        if (oidcAccount == null) {
            return;
        }

        if (!this.oidcAccounts.contains(oidcAccount)) {
            this.oidcAccounts.add(oidcAccount);
        }

        if (oidcAccount.getUser() != this) {
            oidcAccount.assignUser(this);
        }
    }

    public void setCredentialAccount(CredentialAccount credentialAccount) {
        if (credentialAccount == null) {
            return;
        }

        this.credentialAccount = credentialAccount;

        if (credentialAccount.getUser() != this) {
            credentialAccount.assignUser(this);
        }
    }
}
