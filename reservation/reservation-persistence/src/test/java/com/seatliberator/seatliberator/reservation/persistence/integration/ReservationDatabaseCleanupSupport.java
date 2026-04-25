package com.seatliberator.seatliberator.reservation.persistence.integration;

import com.seatliberator.seatliberator.reservation.persistence.book.jpa.repository.ReservationRepository;
import com.seatliberator.seatliberator.reservation.persistence.room.jpa.repository.RoomRepository;
import com.seatliberator.seatliberator.reservation.persistence.room.jpa.repository.SeatRepository;
import com.seatliberator.seatliberator.reservation.persistence.waitlist.jpa.repository.WaitlistRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ReservationDatabaseCleanupSupport {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private RoomRepository roomRepository;

    @AfterEach
    void cleanUpDatabase() {
        waitlistRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        seatRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
    }
}
