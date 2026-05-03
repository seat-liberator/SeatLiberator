package com.seatliberator.seatliberator.reservation.persistence.book.jpa;

import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.*;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.persistence.book.jpa.repository.ReservationRepository;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.CommonPredicates;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.SeatLocatorPredicates;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.TimeRangePredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaReservationPersistenceAdapter implements ReservationStore, ReservationReader {

    private final ReservationRepository repository;

    @Override
    public Reservation save(Reservation reservation) {
        return repository.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(UUID reservationId) {
        return repository.findById(reservationId);
    }

    @Override
    public Optional<Reservation> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public boolean existsOne(ReservationSeatLookupCriteria criteria) {
        var spec = createSpecificationFromFindOneCriteria(criteria);
        return repository.exists(spec);
    }

    @Override
    public Optional<Reservation> findOne(ReservationSeatLookupCriteria criteria) {
        var spec = createSpecificationFromFindOneCriteria(criteria);
        return repository.findOne(spec);
    }

    @Override
    public boolean existsOverlapping(ReservationSeatOverlapCriteria criteria) {
        var spec = createSpecificationFromOverlapCriteria(criteria);
        return repository.exists(spec);
    }

    @Override
    public boolean existsOverlapping(ReservationRoomOverlapCriteria criteria) {
        var spec = createSpecificationFromRoomOverlapCriteria(criteria);
        return repository.exists(spec);
    }

    @Override
    public List<Reservation> findAllOverlapping(ReservationSeatOverlapCriteria criteria) {
        var spec = createSpecificationFromOverlapCriteria(criteria);
        return repository.findAll(spec);
    }

    @Override
    public List<Reservation> findAllOverlapping(ReservationRangeOverlapCriteria criteria) {
        var spec = createSpecificationFromRangeOverlapCriteria(criteria);
        return repository.findAll(spec);
    }

    @Override
    public List<Reservation> findAllOverlapping(ReservationRoomOverlapCriteria criteria) {
        var spec = createSpecificationFromRoomOverlapCriteria(criteria);
        return repository.findAll(spec);
    }

    @Override
    public void delete(Reservation reservation) {
        repository.delete(reservation);
    }

    private Specification<Reservation> createSpecificationFromOverlapCriteria(ReservationSeatOverlapCriteria criteria) {
        return createSpecificationFromFilter(criteria.filter())
                .and(SeatLocatorPredicates.eq(criteria.locator(), SeatLocatorPredicates.defaultLocatorPathFunction()))
                .and(TimeRangePredicates.overlap(criteria.range(), TimeRangePredicates.defaultRangePathFunction()));
    }

    private Specification<Reservation> createSpecificationFromRoomOverlapCriteria(ReservationRoomOverlapCriteria criteria) {
        return createSpecificationFromFilter(criteria.filter())
                .and(TimeRangePredicates.overlap(criteria.range(), TimeRangePredicates.defaultRangePathFunction()))
                .and(CommonPredicates.eq(criteria.roomId(), from -> from.get("locator").get("roomId")));
    }

    private Specification<Reservation> createSpecificationFromRangeOverlapCriteria(ReservationRangeOverlapCriteria criteria) {
        return createSpecificationFromFilter(criteria.filter())
                .and(TimeRangePredicates.overlap(criteria.range(), TimeRangePredicates.defaultRangePathFunction()));
    }

    private Specification<Reservation> createSpecificationFromFindOneCriteria(ReservationSeatLookupCriteria criteria) {
        return createSpecificationFromFilter(criteria.filter())
                .and(SeatLocatorPredicates.eq(criteria.locator(), SeatLocatorPredicates.defaultLocatorPathFunction()))
                .and(TimeRangePredicates.eq(criteria.range(), TimeRangePredicates.defaultRangePathFunction()));
    }

    private Specification<Reservation> createSpecificationFromFilter(ReservationFilter filter) {
        return Specification.<Reservation>unrestricted()
                .and(CommonPredicates.excludeIn(filter.excludedIds(), from -> from.get("id")))
                .and(CommonPredicates.in(filter.userIds(), from -> from.get("userId")))
                .and(CommonPredicates.in(filter.statuses(), from -> from.get("status")));
    }
}