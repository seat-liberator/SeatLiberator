package com.seatliberator.seatliberator.reservation.persistence.waitlist.jpa;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistReader;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistStore;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.filter.WaitlistFilter;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.filter.WaitlistOrder;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification.CommonPredicates;
import com.seatliberator.seatliberator.reservation.persistence.waitlist.jpa.repository.WaitlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaWaitlistStore implements WaitlistStore, WaitlistReader {
    private final WaitlistRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByFilter(WaitlistFilter filter) {
        Preconditions.requireNonNull(filter, "filter");
        return repository.findAll(createSpecification(filter)).stream()
                .anyMatch(waitlist -> matchesSlotIds(waitlist, filter.slotIds()));
    }

    @Override
    public Optional<Waitlist> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Waitlist> findByFilter(WaitlistFilter filter, WaitlistOrder order) {
        Preconditions.requireNonNull(filter, "filter");
        Preconditions.requireNonNull(order, "order");
        return repository.findAll(createSpecification(filter), createSort(order)).stream()
                .filter(waitlist -> matchesSlotIds(waitlist, filter.slotIds()))
                .toList();
    }

    @Override
    public Waitlist save(Waitlist waitlist) {
        return repository.save(waitlist);
    }

    @Override
    public List<Waitlist> saveAll(Iterable<Waitlist> waitlists) {
        return repository.saveAll(waitlists);
    }

    private Specification<Waitlist> createSpecification(WaitlistFilter filter) {
        var spec = Specification.<Waitlist>unrestricted();

        if (filter.userId() != null) {
            spec = spec.and(CommonPredicates.eq(filter.userId(), from -> from.get("userId")));
        }
        if (filter.behavior() != null) {
            spec = spec.and(CommonPredicates.eq(filter.behavior(), from -> from.get("behavior")));
        }
        if (filter.status() != null) {
            spec = spec.and(CommonPredicates.eq(filter.status(), from -> from.get("state").get("status")));
        }
        if (filter.occupancyDate() != null) {
            spec = spec.and(CommonPredicates.eq(filter.occupancyDate(), from -> from.get("occupancyDate")));
        }

        return spec;
    }

    private Sort createSort(WaitlistOrder order) {
        return switch (order.order()) {
            case FIFO -> Sort.by(Sort.Direction.ASC, "state.requestedAt");
            case LIFO, RECENTLY_CREATED -> Sort.by(Sort.Direction.DESC, "state.requestedAt");
        };
    }

    private boolean matchesSlotIds(Waitlist waitlist, Set<UUID> slotIds) {
        if (slotIds.isEmpty()) return true;
        return Set.copyOf(waitlist.getSlotIds()).equals(slotIds);
    }
}
