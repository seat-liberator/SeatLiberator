package com.seatliberator.seatliberator.reservation.application.occupancy.contract;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyStore;
import com.seatliberator.seatliberator.reservation.application.seat.contract.SeatTimeSlotBundlePolicy;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatOccupancyCreator {
    private final SeatOccupancyStore store;

    private final SeatTimeSlotBundlePolicy slotBundlePolicy;
    private final Clock clock;

    public List<SeatOccupancy> create(UUID reservationId, Collection<UUID> slotIds, LocalDate occupancyDate) {
        Preconditions.requireNonNull(reservationId, "reservationId");
        Preconditions.requireNonNull(slotIds, "slotIds");

        slotBundlePolicy.validate(slotIds);

        var now = clock.instant();

        var occupancies = slotIds.stream()
                .map(slotId -> SeatOccupancy.of(slotId, reservationId, occupancyDate, now))
                .toList();

        return store.saveAll(occupancies);
    }
}
