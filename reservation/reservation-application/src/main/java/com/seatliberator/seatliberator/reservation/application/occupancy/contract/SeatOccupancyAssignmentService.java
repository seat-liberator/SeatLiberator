package com.seatliberator.seatliberator.reservation.application.occupancy.contract;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyStore;
import com.seatliberator.seatliberator.reservation.application.seat.contract.SeatTimeSlotBundlePolicy;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatOccupancyAssignmentService {
    private final SeatOccupancyStore store;

    private final SeatTimeSlotBundlePolicy slotBundlePolicy;
    private final Clock clock;

    public List<SeatOccupancy> create(Reservation reservation, Collection<SeatTimeSlot> slots, LocalDate occupancyDate) {
        Preconditions.requireNonNull(reservation, "reservation");
        Preconditions.requireNonNull(slots, "slots");
        Preconditions.requireNonNull(occupancyDate, "occupancyDate");

        var slotBundlePolicyResult = slotBundlePolicy.evaluate(slots);
        if (slotBundlePolicyResult.rejected())
            throw new ReservationApplicationPolicyException(slotBundlePolicyResult.reason());

        var now = clock.instant();

        var occupancies = slots.stream()
                .map(slot -> SeatOccupancy.of(slot, reservation, occupancyDate, now))
                .toList();

        return store.saveAll(occupancies);
    }
}
