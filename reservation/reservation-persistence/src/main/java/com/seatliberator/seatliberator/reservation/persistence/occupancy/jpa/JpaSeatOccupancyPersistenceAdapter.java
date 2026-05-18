package com.seatliberator.seatliberator.reservation.persistence.occupancy.jpa;

import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyReader;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyStore;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria.SeatOccupancyFilter;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria.SeatOccupancySlotCriteria;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import com.seatliberator.seatliberator.reservation.persistence.occupancy.jpa.repository.SeatOccupancyRepository;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.CommonPredicates;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.DateRangePredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaSeatOccupancyPersistenceAdapter implements SeatOccupancyReader, SeatOccupancyStore {
    private final SeatOccupancyRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<SeatOccupancy> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<SeatOccupancy> findByIds(Collection<UUID> id) {
        return repository.findAllById(id);
    }

    @Override
    public List<SeatOccupancy> findByReservationId(UUID reservationId) {
        return repository.findByReservationId(reservationId);
    }

    @Override
    public List<SeatOccupancy> findByCriteria(SeatOccupancySlotCriteria criteria) {
        var spec = createSpecificationFromCriteria(criteria);
        return repository.findAll(spec);
    }

    @Override
    public SeatOccupancy save(SeatOccupancy seatOccupancy) {
        return repository.save(seatOccupancy);
    }

    @Override
    public List<SeatOccupancy> saveAll(Collection<SeatOccupancy> seatOccupancies) {
        return repository.saveAll(seatOccupancies);
    }

    @Override
    public void delete(SeatOccupancy seatOccupancy) {
        repository.delete(seatOccupancy);
    }

    @Override
    public void deleteAll(Collection<SeatOccupancy> seatOccupancies) {
        repository.deleteAllInBatch(seatOccupancies);
    }

    private Specification<SeatOccupancy> createSpecificationFromCriteria(SeatOccupancySlotCriteria criteria) {
        var spec = createSpecificationFromFilter(criteria.filter());

        if (criteria.matchMode().isMatchAnyOf()) {
            spec = spec.and(CommonPredicates.in(criteria.slotIds(), from -> from.get("seatTimeSlotId")));
        } else if (criteria.matchMode().isMatchNoneOf()) {
            spec = spec.and(CommonPredicates.excludeIn(criteria.slotIds(), from -> from.get("seatTimeSlotId")));
        }

        return spec;
    }

    private Specification<SeatOccupancy> createSpecificationFromFilter(SeatOccupancyFilter filter) {
        var spec = Specification.<SeatOccupancy>unrestricted();

        if (filter.reservationId() != null) {
            spec = spec.and(CommonPredicates.eq(filter.reservationId(), from -> from.get("reservationId")));
        }

        if (filter.range() != null) {
            spec = spec.and(DateRangePredicates.contains(filter.range(), from -> from.get("occupancyDate")));
        }

        return spec;
    }
}
