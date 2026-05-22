package com.seatliberator.seatliberator.identity.server.application.credential.port.out;

import com.seatliberator.seatliberator.identity.server.domain.account.CredentialAccount;

public interface CredentialAccountStore {
    CredentialAccount save(CredentialAccount credentialAccount);

    void delete(CredentialAccount credentialAccount);
}
