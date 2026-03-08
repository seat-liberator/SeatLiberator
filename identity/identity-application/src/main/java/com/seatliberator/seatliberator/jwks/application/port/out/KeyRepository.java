package com.seatliberator.seatliberator.jwks.application.port.out;

import com.seatliberator.seatliberator.jwks.domain.SigningKey;

import java.util.List;
import java.util.Optional;

public interface KeyRepository {
    SigningKey getSignableKey();

    Optional<SigningKey> getByKid(String kid);

    List<SigningKey> getAllVerifyOnlyKeys();
}
