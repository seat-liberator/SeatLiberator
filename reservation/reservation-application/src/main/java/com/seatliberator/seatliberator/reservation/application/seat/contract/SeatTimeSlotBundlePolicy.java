package com.seatliberator.seatliberator.reservation.application.seat.contract;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailySchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class SeatTimeSlotBundlePolicy {
    private final SeatTimeSlotReader reader;

    public PolicyResult evaluate(Collection<SeatTimeSlot> slots) {
        Preconditions.requireNonNull(slots, "slots");

        if (slots.isEmpty()) return SimplePolicyResult.reject(SeatTimeSlotPolicyReason.EMPTY_SLOT);
        if (slots.contains(null)) return SimplePolicyResult.reject(SeatTimeSlotPolicyReason.NULL_SLOT_INCLUDED);

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
