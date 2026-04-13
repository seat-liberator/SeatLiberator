package com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationOverlapCriteria;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationRoomOverlapCriteria;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationFindOneCriteria;
import com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa.repository.ReservationRepository;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.CommonPredicates;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.SeatLocatorPredicates;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.TimeRangePredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaReservationPersistenceAdapter implements ReservationStore, ReservationReader {

    private final ReservationRepository repository;

    @Override
    public Reservation save(Reservation reservation) {
        return repository.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(Long reservationId) {
        return repository.findById(reservationId);
    }

    @Override
    public Optional<Reservation> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public boolean existsOne(ReservationFindOneCriteria criteria) {
        var spec = createSpecificationFromFindOneCriteria(criteria);
        return repository.exists(spec);
    }

    @Override
    public Optional<Reservation> findOne(ReservationFindOneCriteria criteria) {
        var spec = createSpecificationFromFindOneCriteria(criteria);
        return repository.findOne(spec);
    }

    @Override
    public boolean existsOverlapping(ReservationOverlapCriteria criteria) {
        var spec = createSpecificationFromOverlapCriteria(criteria);
        return repository.exists(spec);
    }

    @Override
    public boolean existsOverlapping(ReservationRoomOverlapCriteria criteria) {
        var spec = createSpecificationFromRoomOverlapCriteria(criteria);
        return repository.exists(spec);
    }

    @Override
    public List<Reservation> findAllOverlapping(ReservationOverlapCriteria criteria) {
        var spec = createSpecificationFromOverlapCriteria(criteria);
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

    private Specification<Reservation> createSpecificationFromOverlapCriteria(ReservationOverlapCriteria criteria) {

        return Specification.<Reservation>unrestricted()
                .and(SeatLocatorPredicates.eq(criteria.locator(), SeatLocatorPredicates.defaultLocatorPathFunction()))
                .and(TimeRangePredicates.overlap(criteria.range(), TimeRangePredicates.defaultRangePathFunction()))
                .and(CommonPredicates.in(criteria.statuses(), from -> from.get("status")))
                .and(CommonPredicates.excludeIn(criteria.excludedIds(), from -> from.get("id")));
    }

    private Specification<Reservation> createSpecificationFromFindOneCriteria(ReservationFindOneCriteria criteria) {
        return Specification.<Reservation>unrestricted()
                .and(SeatLocatorPredicates.eq(criteria.locator(), SeatLocatorPredicates.defaultLocatorPathFunction()))
                .and(TimeRangePredicates.eq(criteria.range(), TimeRangePredicates.defaultRangePathFunction()))
                .and(CommonPredicates.in(criteria.statuses(), from -> from.get("status")));
    }

    private Specification<Reservation> createSpecificationFromRoomOverlapCriteria(ReservationRoomOverlapCriteria criteria) {
        return Specification.<Reservation>unrestricted()
                .and(TimeRangePredicates.overlap(criteria.range(), TimeRangePredicates.defaultRangePathFunction()))
                .and(CommonPredicates.eq(criteria.roomId(), from -> from.get("locator").get("roomId")));
    }
}
