package com.seatliberator.seatliberator.reservation.waitlist.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface WaitlistRepository extends JpaRepository<Waitlist, UUID>, JpaSpecificationExecutor<Waitlist> {
}
