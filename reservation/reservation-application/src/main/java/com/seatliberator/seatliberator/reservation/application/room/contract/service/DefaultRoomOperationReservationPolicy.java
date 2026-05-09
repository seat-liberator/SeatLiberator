package com.seatliberator.seatliberator.reservation.application.room.contract.service;

import com.seatliberator.seatliberator.reservation.application.room.contract.RoomOperationReservationPolicy;
import com.seatliberator.seatliberator.reservation.application.room.contract.result.RoomPolicyReason;
import com.seatliberator.seatliberator.reservation.application.room.contract.result.RoomPolicyResult;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegment;
import com.seatliberator.seatliberator.reservation.domain.shared.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DefaultRoomOperationReservationPolicy implements RoomOperationReservationPolicy {
    private final RoomReader roomReader;
    private final Clock clock;

    @Override
    public RoomPolicyResult evaluate(SeatLocator locator, InstantRange range) {
        return roomReader.findByRoomId(locator.roomId())
                .map(room -> evaluate(room.getOperationPolicy(), range))
                .orElseGet(() -> RoomPolicyResult.reject(RoomPolicyReason.ROOM_NOT_FOUND));
    }

    private RoomPolicyResult evaluate(RoomOperationPolicy policy, InstantRange range) {
        if (policy.getOperationStatus() != RoomOperationStatus.OPEN) {
            return RoomPolicyResult.reject(RoomPolicyReason.ROOM_OPERATION_CLOSED);
        }

        if (Duration.between(range.startAt(), range.endAt()).compareTo(policy.getMaxReservationDuration()) > 0) {
            return RoomPolicyResult.reject(RoomPolicyReason.MAX_RESERVATION_DURATION_EXCEEDED);
        }

        if (!isWithinOperationTime(policy, range)) {
            return RoomPolicyResult.reject(RoomPolicyReason.OUT_OF_OPERATION_HOURS);
        }

        return RoomPolicyResult.accept(RoomPolicyReason.ROOM_OPERATION_AVAILABLE);
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
            reservationEndNanoOfDay = DailyTimeSegment.DAY_NANOS;
        } else if (!startDate.equals(endDate)) {
            return false;
        } else {
            reservationEndNanoOfDay = rawEndNanoOfDay;
        }

        var reservationStartNanoOfDay = start.toLocalTime().toNanoOfDay();

        return policy.getOperationTimeSegments().stream()
                .anyMatch(segment -> contains(segment, reservationStartNanoOfDay, reservationEndNanoOfDay));
    }

    private boolean contains(DailyTimeSegment segment, long startNanoOfDay, long endNanoOfDay) {
        return segment.startNanoOfDay() <= startNanoOfDay
                && endNanoOfDay <= segment.endNanoOfDay();
    }
}
