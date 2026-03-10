package com.seatliberator.seatliberator.identity.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.identity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
