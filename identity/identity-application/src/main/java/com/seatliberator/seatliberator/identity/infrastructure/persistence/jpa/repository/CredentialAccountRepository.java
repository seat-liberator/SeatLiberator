package com.seatliberator.seatliberator.identity.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.identity.domain.CredentialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CredentialAccountRepository extends JpaRepository<CredentialAccount, UUID> {
    Optional<CredentialAccount> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT c
            FROM CredentialAccount c
            JOIN FETCH c.user
            WHERE c.email = :email""")
    Optional<CredentialAccount> findAuthByEmail(
            @Param("email") String email
    );
}