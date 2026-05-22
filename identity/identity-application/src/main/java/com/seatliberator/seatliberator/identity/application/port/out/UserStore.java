package com.seatliberator.seatliberator.identity.application.port.out;

import com.seatliberator.seatliberator.identity.server.domain.account.User;

import java.util.Optional;
import java.util.UUID;

public interface UserStore {
    boolean existsById(UUID userId);

    Optional<User> findById(UUID userId);

    User save(User user);
}
