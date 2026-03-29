package com.seatliberator.seatliberator.reservation.vacancy.application.port.out;

import com.seatliberator.seatliberator.reservation.vacancy.domain.VacancyAlertRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VacancyAlertRequestReader {
    boolean existsActiveRequestFor(
            String userId,
            String roomId,
            String seatId,
            Instant targetStartTime,
            Instant targetEndTime
    );

    Optional<VacancyAlertRequest> findById(UUID id);

    List<VacancyAlertRequest> findActiveBySeatAndTimeRange(
            String seatId,
            Instant startTime,
            Instant endTime
    );
}
