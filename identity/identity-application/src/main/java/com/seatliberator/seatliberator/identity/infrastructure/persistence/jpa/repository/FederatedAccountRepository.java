package com.seatliberator.seatliberator.identity.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.identity.domain.FederatedAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FederatedAccountRepository extends JpaRepository<FederatedAccount, UUID> {
    Optional<FederatedAccount> findByRegistrationIdAndProviderUserId(
            String registrationId,
            String providerUserId
    );

    @Query("""
            SELECT f
            FROM FederatedAccount f
            JOIN FETCH f.user
            WHERE f.registrationId = :registrationId
                AND f.providerUserId = :providerUserId""")
    Optional<FederatedAccount> findAuthByRegistrationIdAndProviderUserId(
            @Param("registrationId") String registrationId,
            @Param("providerUserId") String providerUserId
    );

    boolean existsByRegistrationIdAndProviderUserId(
            String registrationId,
            String providerUserId
    );
}
