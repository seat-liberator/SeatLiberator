package com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationCriteria;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationExclusion;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationTarget;
import com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa.repository.ReservationRepository;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.CommonPredicates;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.SeatLocatorPredicates;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.TimeRangePredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaReservationStore implements ReservationStore, ReservationReader {

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
    public Optional<Reservation> findOne(ReservationCriteria criteria) {
        var spec = createLocatorAndRangeSpecification(criteria.locator(), criteria.range())
                .and(CommonPredicates.eq(criteria.status(), from -> from.get("status")));
        return repository.findOne(spec);
    }

    @Override
    public boolean existsOverlapping(ReservationTarget target) {
        var spec = createLocatorAndRangeSpecification(target.locator(), target.ranga());
        return repository.exists(spec);
    }

    @Override
    public boolean existsOverlapping(ReservationTarget target, ReservationExclusion exclusion) {
        var spec = createLocatorAndRangeSpecification(target.locator(), target.ranga())
                .and(CommonPredicates.excludeIn(exclusion.ids(), from -> from.get("id")));
        return repository.exists(spec);
    }

    @Override
    public List<Reservation> findAllOverlapping(TimeRange range) {
        var spec = Specification.<Reservation>unrestricted()
                .and(TimeRangePredicates.overlap(range, TimeRangePredicates.defaultRangePathFunction()));
        return repository.findAll(spec);
    }

    @Override
    public List<Reservation> findAllOverlappingInRoom(String roomId, TimeRange range) {
        var spec = Specification.<Reservation>unrestricted()
                .and(TimeRangePredicates.overlap(range, TimeRangePredicates.defaultRangePathFunction()))
                .and(CommonPredicates.eq(roomId, from -> from.get("locator").get("roomId")));
        return repository.findAll(spec);
    }

    @Override
    public Optional<Reservation> findReservationBySeatAt(String roomId, String seatId, Instant startTime, Instant endTime) {
        return repository.findReservationBySeatAt(roomId, seatId, startTime, endTime);
    }

    @Override
    public Optional<Reservation> findByLocatorAndRangeAndStatus(SeatLocator locator, TimeRange range, ReservationStatus status) {
        var spec = createLocatorAndRangeSpecification(locator, range)
                .and(CommonPredicates.eq(status, from -> from.get("status")));
        return repository.findOne(spec);
    }

    @Override
    public void delete(Reservation reservation) {
        repository.delete(reservation);
    }

    @Override
    public boolean existsByLocatorAndRange(SeatLocator locator, TimeRange range) {
        var spec = createLocatorAndRangeSpecification(locator, range);
        return repository.exists(spec);
    }

    @Override
    public boolean existsByLocatorAndRangeAndStatus(SeatLocator locator, TimeRange range, ReservationStatus status) {
        var spec = createLocatorAndRangeSpecification(locator, range)
                .and(CommonPredicates.eq(status, from -> from.get("status")));
        return repository.exists(spec);
    }

    @Override
    public boolean existsByLocatorAndRangeWithExcludeIds(SeatLocator locator, TimeRange range, Collection<Long> ids) {
        var spec = createLocatorAndRangeSpecification(locator, range)
                .and(CommonPredicates.excludeIn(ids, from -> from.get("id")));
        return repository.exists(spec);
    }

    private Specification<Reservation> createLocatorAndRangeSpecification(SeatLocator locator, TimeRange range) {
        return Specification.<Reservation>unrestricted()
                .and(SeatLocatorPredicates.eq(locator, SeatLocatorPredicates.defaultLocatorPathFunction()))
                .and(TimeRangePredicates.overlap(range, TimeRangePredicates.defaultRangePathFunction()));
    }
}
