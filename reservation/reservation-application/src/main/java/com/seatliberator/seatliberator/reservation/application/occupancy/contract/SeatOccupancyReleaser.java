package com.seatliberator.seatliberator.reservation.application.occupancy.contract;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyReader;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SeatOccupancyReleaser {
    private final SeatOccupancyReader reader;
    private final SeatOccupancyStore store;

    public SeatOccupancyReleasedResult release(UUID reservationId) {
        Preconditions.requireNonNull(reservationId, "reservationId");

        var occupancies = reader.findByReservationId(reservationId);

        if (occupancies.isEmpty()) {
            throw new ReservationApplicationPolicyException(SeatOccupancyPolicyReason.EMPTY_OCCUPANCIES);
        }

        var occupancyDates = occupancies.stream()
                .map(SeatOccupancy::getOccupancyDate)
                .distinct()
                .toList();

        var releasedSlotIds = occupancies.stream()
                .map(SeatOccupancy::getSeatTimeSlotId)
                .collect(Collectors.toUnmodifiableSet());

        if (occupancyDates.size() != 1) {
            throw new ReservationApplicationPolicyException(SeatOccupancyPolicyReason.DIFFERENT_OCCUPANCY_DATE_INCLUDED);
        }

        store.deleteAll(occupancies);

        var occupancyDate = occupancyDates.getFirst();

        return SeatOccupancyReleasedResult.of(reservationId, releasedSlotIds, occupancyDate);
    }
}
