package com.seatliberator.seatliberator.identity.server.persistence.user.jpa.repository;

import com.seatliberator.seatliberator.identity.server.domain.account.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
