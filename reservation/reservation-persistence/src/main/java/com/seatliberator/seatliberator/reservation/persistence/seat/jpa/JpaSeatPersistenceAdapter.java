package com.seatliberator.seatliberator.reservation.persistence.seat.jpa;

import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.criteria.SeatLookupCriteria;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatFilter;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository.SeatRepository;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.CommonPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaSeatPersistenceAdapter implements SeatStore, SeatReader {
    private final SeatRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByCriteria(SeatLookupCriteria criteria) {
        var spec = createSpecificationFromCriteria(criteria);
        return repository.exists(spec);
    }

    @Override
    public Optional<Seat> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Seat> findByFilter(SeatFilter filter) {
        var spec = createSpecificationFromFilter(filter);
        return repository.findAll(spec);
    }

    @Override
    public Seat save(Seat seat) {
        return repository.save(seat);
    }

    @Override
    public void delete(Seat seat) {
        repository.delete(seat);
    }

    private Specification<Seat> createSpecificationFromFilter(SeatFilter filter) {
        var spec = Specification.<Seat>unrestricted();

        if (filter.roomId() != null) {
            spec = spec.and(CommonPredicates.eq(filter.roomId(), from -> from.get("roomId")));
        }

        return spec;
    }

    private Specification<Seat> createSpecificationFromCriteria(SeatLookupCriteria criteria) {
        var spec = Specification.<Seat>unrestricted();

        spec = spec.and(CommonPredicates.eq(criteria.roomId(), from -> from.get("roomId")));
        spec = spec.and(CommonPredicates.eq(criteria.seatCode(), from -> from.get("code")));

        return spec;
    }
}
