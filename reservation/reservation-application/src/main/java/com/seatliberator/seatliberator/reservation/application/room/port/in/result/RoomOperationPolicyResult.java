package com.seatliberator.seatliberator.reservation.application.room.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleDailyTimeSegment;

import java.time.Duration;
import java.util.List;

public record RoomOperationPolicyResult(
        Integer maxReservationPerUser,
        Duration maxReservationDuration,
        RoomOperationStatus operationStatus,
        List<SimpleDailyTimeSegment> operationTimeSegments
) {
    public static RoomOperationPolicyResult from(RoomOperationPolicy operationPolicy) {
        return new RoomOperationPolicyResult(
                operationPolicy.getMaxReservationPerUser(),
                operationPolicy.getMaxReservationDuration(),
                operationPolicy.getOperationStatus(),
                operationPolicy.getOperationTimeSegments().stream()
                        .map(SimpleDailyTimeSegment::from)
                        .toList()
        );
    }
}
