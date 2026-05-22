package com.seatliberator.seatliberator.identity.server.persistence.account.jpa;


import com.seatliberator.seatliberator.identity.server.application.account.port.out.UserStore;
import com.seatliberator.seatliberator.identity.server.domain.account.User;
import com.seatliberator.seatliberator.identity.server.persistence.account.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserStore implements UserStore {
    private final UserRepository userRepository;

    @Override
    public boolean existsById(UUID userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
