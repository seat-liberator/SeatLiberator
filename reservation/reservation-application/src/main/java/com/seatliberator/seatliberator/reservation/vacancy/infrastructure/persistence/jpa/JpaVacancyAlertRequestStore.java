package com.seatliberator.seatliberator.reservation.vacancy.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.CommonPredicates;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.SeatLocatorPredicates;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.TimeRangePredicates;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import com.seatliberator.seatliberator.reservation.vacancy.infrastructure.persistence.jpa.repository.VacancyAlertRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaVacancyAlertRequestStore implements
        VacancyAlertRequestStore {
    private final VacancyAlertRequestRepository repository;

    @Override
    public List<VacancyAlertRequest> findByLocatorAndRangeAndStatus(SeatLocator locator, TimeRange range, VacancyAlertRequestStatus status) {
        var spec = createLocatorAndRangeSpecification(locator, range)
                .and(CommonPredicates.eq(status, from -> from.get("state").get("status")));
        return repository.findAll(spec);
    }

    @Override
    public boolean existsByUserIdAndLocatorAndRangeAndStatus(String userId, SeatLocator locator, TimeRange range, VacancyAlertRequestStatus status) {
        var spec = Specification.<VacancyAlertRequest>unrestricted()
                .and(SeatLocatorPredicates.eq(locator, from -> from.get("locator")))
                .and(TimeRangePredicates.eq(range, from -> from.get("range")))
                .and(CommonPredicates.eq(userId, from -> from.get("userId")))
                .and(CommonPredicates.eq(status, from -> from.get("state").get("status")));
        return repository.exists(spec);
    }

    @Override
    public Optional<VacancyAlertRequest> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<VacancyAlertRequest> findByLocatorAndRange(SeatLocator locator, TimeRange range) {
        var spec = createLocatorAndRangeSpecification(locator, range);
        return repository.findAll(spec);
    }

    @Override
    public VacancyAlertRequest save(VacancyAlertRequest vacancyAlertRequest) {
        return repository.save(vacancyAlertRequest);
    }

    @Override
    public List<VacancyAlertRequest> saveAll(Iterable<VacancyAlertRequest> vacancyAlertRequests) {
        return repository.saveAll(vacancyAlertRequests);
    }

    private Specification<VacancyAlertRequest> createLocatorAndRangeSpecification(SeatLocator locator, TimeRange range) {
        return Specification.<VacancyAlertRequest>unrestricted()
                .and(SeatLocatorPredicates.eq(locator, from -> from.get("locator")))
                .and(TimeRangePredicates.withIn(range, from -> from.get("range")));
    }
}
