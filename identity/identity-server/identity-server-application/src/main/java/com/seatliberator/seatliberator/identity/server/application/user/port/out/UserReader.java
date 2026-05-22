package com.seatliberator.seatliberator.identity.server.application.user.port.out;

import com.seatliberator.seatliberator.identity.server.domain.account.User;

import java.util.Optional;
import java.util.UUID;

public interface UserReader {
    boolean existsById(UUID id);

    Optional<User> findById(UUID id);
}
