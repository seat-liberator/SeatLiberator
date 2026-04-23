package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa.repository.ReservationRepository;
import com.seatliberator.seatliberator.reservation.room.infrastructure.persistence.jpa.repository.SeatRepository;
import com.seatliberator.seatliberator.reservation.waitlist.infrastructure.persistence.jpa.repository.WaitlistRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ReservationDatabaseCleanupSupport {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    @AfterEach
    void cleanUpDatabase() {
        waitlistRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        seatRepository.deleteAllInBatch();
    }
}
