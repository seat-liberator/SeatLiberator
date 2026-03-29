package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa.repository.ReservationRepository;
import com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa.repository.SeatRepository;
import com.seatliberator.seatliberator.reservation.vacancy.infrastructure.persistence.jpa.repository.VacancyAlertRequestRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ReservationDatabaseCleanupSupport {

    @Autowired
    private VacancyAlertRequestRepository vacancyAlertRequestRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    @AfterEach
    void cleanUpDatabase() {
        vacancyAlertRequestRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        seatRepository.deleteAllInBatch();
    }
}
