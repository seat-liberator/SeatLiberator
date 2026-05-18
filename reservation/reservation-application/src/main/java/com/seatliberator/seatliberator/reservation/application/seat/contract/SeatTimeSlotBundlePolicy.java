package com.seatliberator.seatliberator.reservation.application.seat.contract;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailySchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SeatTimeSlotBundlePolicy {
    private final SeatTimeSlotReader reader;

    public void validate(Collection<UUID> slotIds) {
        Preconditions.requireNonNull(slotIds, "slotIds");
        if (slotIds.isEmpty())
            throw new ReservationApplicationPolicyException(SeatTimeSlotPolicyReason.EMPTY_SLOT);

        var dedupIds = slotIds.stream()
                .collect(Collectors.toUnmodifiableSet());
        if (slotIds.size() != dedupIds.size())
            throw new ReservationApplicationPolicyException(SeatTimeSlotPolicyReason.DUPLICATE_SLOT);

        var slots = reader.findByIds(slotIds).stream()
                .collect(Collectors.toUnmodifiableSet());
        if (dedupIds.size() != slots.size())
            throw new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_TIME_SLOT_NOT_FOUND);

        validate(slots);
    }

    public void validate(Set<SeatTimeSlot> slots) {
        var result = evaluate(slots);
        if (result.rejected())
            throw new ReservationApplicationPolicyException(result.reason());
    }

    public PolicyResult evaluate(Set<SeatTimeSlot> slots) {
        Preconditions.requireNonNull(slots, "slots");

        if (!areAllActive(slots)) return SimplePolicyResult.reject(SeatTimeSlotPolicyReason.INACTIVE_SLOT_INCLUDED);
        if (!belongsToSameSeat(slots)) return SimplePolicyResult.reject(SeatTimeSlotPolicyReason.DIFFERENT_SEAT_INCLUDED);

        var ranges = slots.stream()
                .map(SeatTimeSlot::getSlotRange)
                .toList();

        if (!DailySchedule.isContinuous(ranges)) return SimplePolicyResult.reject(SeatTimeSlotPolicyReason.DISCONTINUOUS_TIME_SLOTS);

        return SimplePolicyResult.accept(SeatTimeSlotPolicyReason.SLOT_BUNDLE_RESERVABLE);
    }

    private boolean areAllActive(Collection<SeatTimeSlot> slots) {
        return slots.stream()
                .allMatch(slot -> slot.getSlotStatus() == SeatTimeSlotStatus.ACTIVE);
    }

    private boolean belongsToSameSeat(Collection<SeatTimeSlot> slots) {
        return slots.stream()
                .map(SeatTimeSlot::getSeatId)
                .distinct()
                .count() == 1;
    }
}
