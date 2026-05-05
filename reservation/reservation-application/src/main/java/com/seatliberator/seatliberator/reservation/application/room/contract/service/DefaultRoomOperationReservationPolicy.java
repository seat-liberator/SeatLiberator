package com.seatliberator.seatliberator.reservation.application.room.contract.service;

import com.seatliberator.seatliberator.reservation.application.room.contract.RoomOperationReservationPolicy;
import com.seatliberator.seatliberator.reservation.application.room.contract.result.RoomPolicyReason;
import com.seatliberator.seatliberator.reservation.application.room.contract.result.RoomPolicyResult;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.EmbeddableDailyTimeWindow;
import com.seatliberator.seatliberator.reservation.domain.shared.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

        if (!contains(policy.getOperationHours(), range)) {
            return RoomPolicyResult.reject(RoomPolicyReason.OUT_OF_OPERATION_HOURS);
        }

        return RoomPolicyResult.accept(RoomPolicyReason.ROOM_OPERATION_AVAILABLE);
    }

    private boolean contains(EmbeddableDailyTimeWindow operationHours, InstantRange range) {
        var startAt = LocalDateTime.ofInstant(range.startAt(), clock.getZone());
        var endAt = LocalDateTime.ofInstant(range.endAt(), clock.getZone());
        var openAt = operationHours.startAt();
        var closeAt = operationHours.endAt();

        if (openAt.equals(closeAt)) {
            return true;
        }

        var windowStart = windowStartFor(startAt, openAt, closeAt);
        if (windowStart == null) {
            return false;
        }

        var windowEnd = closeAt.isAfter(openAt)
                ? LocalDateTime.of(windowStart.toLocalDate(), closeAt)
                : LocalDateTime.of(windowStart.toLocalDate().plusDays(1), closeAt);

        return !endAt.isAfter(windowEnd);
    }

    private LocalDateTime windowStartFor(LocalDateTime startAt, LocalTime openAt, LocalTime closeAt) {
        var requestedTime = startAt.toLocalTime();

        if (closeAt.isAfter(openAt)) {
            if (requestedTime.isBefore(openAt) || !requestedTime.isBefore(closeAt)) {
                return null;
            }
            return LocalDateTime.of(startAt.toLocalDate(), openAt);
        }

        if (!requestedTime.isBefore(openAt)) {
            return LocalDateTime.of(startAt.toLocalDate(), openAt);
        }

        if (requestedTime.isBefore(closeAt)) {
            return LocalDateTime.of(startAt.toLocalDate().minusDays(1), openAt);
        }

        return null;
    }
}
