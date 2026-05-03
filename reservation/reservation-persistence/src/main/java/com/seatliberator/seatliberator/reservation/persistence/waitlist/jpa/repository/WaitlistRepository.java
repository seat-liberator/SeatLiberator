package com.seatliberator.seatliberator.reservation.persistence.waitlist.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface WaitlistRepository extends JpaRepository<Waitlist, UUID>, JpaSpecificationExecutor<Waitlist> {
}
