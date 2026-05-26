package com.seatliberator.seatliberator.reservation.persistence.seat.jpa;

import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotStore;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotFilter;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotRangeOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository.SeatTimeSlotRepository;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.CommonPredicates;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.DailyNanoRangePathPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaSeatTimeSlotPersistenceAdapter implements SeatTimeSlotReader, SeatTimeSlotStore {
    private final SeatTimeSlotRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByCriteria(SeatTimeSlotRangeOverlapCriteria criteria) {
        var spec = createSpecificationFromCriteria(criteria);
        return repository.exists(spec);
    }

    @Override
    public Optional<SeatTimeSlot> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<SeatTimeSlot> findByIds(Collection<UUID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public List<SeatTimeSlot> findByFilter(SeatTimeSlotFilter filter) {
        var spec = createSpecificationFromFilter(filter);
        return repository.findAll(spec);
    }

    @Override
    public SeatTimeSlot save(SeatTimeSlot seatTimeSlot) {
        return repository.save(seatTimeSlot);
    }

    @Override
    public void delete(SeatTimeSlot seatTimeSlot) {
        repository.delete(seatTimeSlot);
    }

    private Specification<SeatTimeSlot> createSpecificationFromFilter(SeatTimeSlotFilter filter) {
        var spec = Specification.<SeatTimeSlot>unrestricted();

        if (filter.seatId() != null) {
            spec = spec.and(CommonPredicates.eq(filter.seatId(), from -> from.get("seatId")));
        }

        return spec;
    }

    private Specification<SeatTimeSlot> createSpecificationFromCriteria(SeatTimeSlotRangeOverlapCriteria criteria) {
        var spec = createSpecificationFromFilter(criteria.filter());

        spec = spec.and(DailyNanoRangePathPredicates.overlapsRange(criteria.range(), from -> from.get("slotRange")));


        return spec;
    }
}
