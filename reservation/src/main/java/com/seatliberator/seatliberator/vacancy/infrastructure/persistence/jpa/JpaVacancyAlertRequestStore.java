package com.seatliberator.seatliberator.vacancy.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.vacancy.application.port.out.VacancyAlertRequestReader;
import com.seatliberator.seatliberator.vacancy.application.port.out.VacancyAlertRequestStore;
import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertRequest;
import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertStatus;
import com.seatliberator.seatliberator.vacancy.infrastructure.persistence.jpa.repository.VacancyAlertRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaVacancyAlertRequestStore implements
        VacancyAlertRequestStore,
        VacancyAlertRequestReader {
    private final VacancyAlertRequestRepository repository;

    @Override
    public boolean existsActiveRequestFor(String userId,String roomId, String seatId, Instant targetStartTime, Instant targetEndTime) {
        return repository.existsRequestFor(
                userId,
                roomId,
                seatId,
                targetStartTime,
                targetEndTime,
                VacancyAlertStatus.ACTIVE
        );
    }

    @Override
    public Optional<VacancyAlertRequest> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<VacancyAlertRequest> findActiveBySeatAndTimeRange(String seatId, Instant startTime, Instant endTime) {
        return repository.findAllRequestsBySeatAndTimeRange(
                seatId,
                startTime,
                endTime,
                VacancyAlertStatus.ACTIVE
        );
    }

    @Override
    public VacancyAlertRequest save(VacancyAlertRequest vacancyAlertRequest) {
        return repository.save(vacancyAlertRequest);
    }
}
