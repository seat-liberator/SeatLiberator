package com.seatliberator.seatliberator.reservation.persistence.book.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID>, JpaSpecificationExecutor<Reservation> {
    List<Reservation> findByUserId(String userId);
}
