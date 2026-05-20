package com.seatliberator.seatliberator.reservation.persistence.book.jpa;

import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.filter.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.filter.ReservationStateFilter;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.persistence.book.jpa.repository.ReservationRepository;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.CommonPredicates;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.InstantPathPredicates;
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
    public List<Reservation> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<Reservation> findByFilter(ReservationFilter filter) {
        var spec = createSpecificationFromFilter(filter);
        return repository.findAll(spec);
    }

    @Override
    public void delete(Reservation reservation) {
        repository.delete(reservation);
    }

    private Specification<Reservation> createSpecificationFromFilter(ReservationFilter filter) {
        var spec = Specification.<Reservation>unrestricted();

        if (filter.userId() != null) {
            spec = spec.and(CommonPredicates.eq(filter.userId(), from -> from.get("userId")));
        }

        if (filter.state() != null) {
            spec = spec.and(createSpecificationFromStateFilter(filter.state()));
        }

        return spec;
    }

    private Specification<Reservation> createSpecificationFromStateFilter(ReservationStateFilter filter) {
        var spec = Specification.<Reservation>unrestricted();

        spec = spec.and(CommonPredicates.eq(filter.status(), from -> from.get("state").get("status")));

        if (filter.range() != null) {
            var auditFieldName = auditFieldNameOf(filter.status());
            spec = spec.and(InstantPathPredicates.containedInRange(filter.range(), from -> from.get("state").get(auditFieldName)));
        }

        return spec;
    }

    private String auditFieldNameOf(ReservationStatus status) {
        return switch (status) {
            case RESERVED -> "reservedAt";
            case USED -> "usedAt";
            case EXPIRED -> "expiredAt";
            case CANCELLED -> "cancelledAt";
        };
    }
}
