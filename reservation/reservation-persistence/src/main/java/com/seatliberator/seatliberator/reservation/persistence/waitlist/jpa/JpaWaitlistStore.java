package com.seatliberator.seatliberator.reservation.persistence.waitlist.jpa;

import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistStore;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.WaitlistStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.CommonPredicates;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.SeatLocatorPredicates;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.TimeRangePredicates;
import com.seatliberator.seatliberator.reservation.persistence.waitlist.jpa.repository.WaitlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaWaitlistStore implements
        WaitlistStore {
    private final WaitlistRepository repository;

    @Override
    public List<Waitlist> findByLocatorAndRangeAndStatus(SeatLocator locator, TimeRange range, WaitlistStatus status) {
        var spec = createLocatorAndRangeSpecification(locator, range)
                .and(CommonPredicates.eq(status, from -> from.get("state").get("status")));
        return repository.findAll(spec);
    }

    @Override
    public boolean existsByUserIdAndLocatorAndRangeAndStatus(String userId, SeatLocator locator, TimeRange range, WaitlistStatus status) {
        var spec = Specification.<Waitlist>unrestricted()
                .and(SeatLocatorPredicates.eq(locator, from -> from.get("locator")))
                .and(TimeRangePredicates.eq(range, from -> from.get("range")))
                .and(CommonPredicates.eq(userId, from -> from.get("userId")))
                .and(CommonPredicates.eq(status, from -> from.get("state").get("status")));
        return repository.exists(spec);
    }

    @Override
    public Optional<Waitlist> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Waitlist> findByLocatorAndRange(SeatLocator locator, TimeRange range) {
        var spec = createLocatorAndRangeSpecification(locator, range);
        return repository.findAll(spec);
    }

    @Override
    public Waitlist save(Waitlist waitlist) {
        return repository.save(waitlist);
    }

    @Override
    public List<Waitlist> saveAll(Iterable<Waitlist> waitlists) {
        return repository.saveAll(waitlists);
    }

    private Specification<Waitlist> createLocatorAndRangeSpecification(SeatLocator locator, TimeRange range) {
        return Specification.<Waitlist>unrestricted()
                .and(SeatLocatorPredicates.eq(locator, from -> from.get("locator")))
                .and(TimeRangePredicates.overlap(range, from -> from.get("range")));
    }
}
