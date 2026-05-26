package com.seatliberator.seatliberator.reservation.application.room.contract;

import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailySchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RoomOperationReservationPolicy {
    private final Clock clock;

    public PolicyResult evaluate(RoomOperationPolicy policy, InstantRange range) {
        if (policy.getOperationStatus() != RoomOperationStatus.OPEN) {
            return SimplePolicyResult.reject(RoomPolicyReason.ROOM_OPERATION_CLOSED);
        }

        if (Duration.between(range.startAt(), range.endAt()).compareTo(policy.getMaxReservationDuration()) > 0) {
            return SimplePolicyResult.reject(RoomPolicyReason.MAX_RESERVATION_DURATION_EXCEEDED);
        }

        var schedule = SimpleDailySchedule.of(policy.getOperationSchedule());

        if (!schedule.contains(range, clock.getZone())) {
            return SimplePolicyResult.reject(RoomPolicyReason.OUT_OF_OPERATION_HOURS);
        }

        return SimplePolicyResult.accept(RoomPolicyReason.ROOM_OPERATION_AVAILABLE);
    }
}
