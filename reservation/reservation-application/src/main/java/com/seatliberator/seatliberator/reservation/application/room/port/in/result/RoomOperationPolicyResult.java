package com.seatliberator.seatliberator.reservation.application.room.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;

import java.time.Duration;
import java.util.List;

public record RoomOperationPolicyResult(
        Integer maxReservationPerUser,
        Duration maxReservationDuration,
        RoomOperationStatus operationStatus,
        List<SimpleDailyNanoRange> operationSchedule
) {
    public static RoomOperationPolicyResult from(RoomOperationPolicy operationPolicy) {
        return new RoomOperationPolicyResult(
                operationPolicy.getMaxReservationPerUser(),
                operationPolicy.getMaxReservationDuration(),
                operationPolicy.getOperationStatus(),
                operationPolicy.getOperationSchedule().stream()
                        .map(SimpleDailyNanoRange::from)
                        .toList()
        );
    }
}
