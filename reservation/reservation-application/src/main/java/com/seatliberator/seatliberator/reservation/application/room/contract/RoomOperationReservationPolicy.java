package com.seatliberator.seatliberator.reservation.application.room.contract;

import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RoomOperationReservationPolicy {
    private final RoomReader roomReader;
    private final Clock clock;

    public PolicyResult evaluate(SeatLocator locator, InstantRange range) {
        return roomReader.findByRoomId(locator.roomId())
                .map(room -> evaluate(room.getOperationPolicy(), range))
                .orElseGet(() -> SimplePolicyResult.reject(RoomPolicyReason.ROOM_NOT_FOUND));
    }

    private PolicyResult evaluate(RoomOperationPolicy policy, InstantRange range) {
        if (policy.getOperationStatus() != RoomOperationStatus.OPEN) {
            return SimplePolicyResult.reject(RoomPolicyReason.ROOM_OPERATION_CLOSED);
        }

        if (Duration.between(range.startAt(), range.endAt()).compareTo(policy.getMaxReservationDuration()) > 0) {
            return SimplePolicyResult.reject(RoomPolicyReason.MAX_RESERVATION_DURATION_EXCEEDED);
        }

        if (!isWithinOperationTime(policy, range)) {
            return SimplePolicyResult.reject(RoomPolicyReason.OUT_OF_OPERATION_HOURS);
        }

        return SimplePolicyResult.accept(RoomPolicyReason.ROOM_OPERATION_AVAILABLE);
    }

    private boolean isWithinOperationTime(RoomOperationPolicy policy, InstantRange range) {
        var zoneId = clock.getZone();
        var start = range.startAt().atZone(zoneId);
        var end = range.endAt().atZone(zoneId);

        var startDate = LocalDate.from(start);
        var endDate = LocalDate.from(end);
        var rawEndNanoOfDay = end.toLocalTime().toNanoOfDay();
        long reservationEndNanoOfDay;

        if (startDate.plusDays(1).equals(endDate) && rawEndNanoOfDay == 0) {
            reservationEndNanoOfDay = DailyNanoRange.DAY_NANOS;
        } else if (!startDate.equals(endDate)) {
            return false;
        } else {
            reservationEndNanoOfDay = rawEndNanoOfDay;
        }

        var reservationStartNanoOfDay = start.toLocalTime().toNanoOfDay();

        return policy.getOperationSchedule().stream()
                .anyMatch(nanoRange -> contains(nanoRange, reservationStartNanoOfDay, reservationEndNanoOfDay));
    }

    private boolean contains(DailyNanoRange range, long startNanoOfDay, long endNanoOfDay) {
        return range.startNanoOfDay() <= startNanoOfDay
                && endNanoOfDay <= range.endNanoOfDay();
    }
}
