package com.seatliberator.seatliberator.reservation.application.room.port.in.result;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;

import java.time.Duration;
import java.time.LocalTime;

public record RoomOperationPolicyResult(
        Integer maxReservationPerUser,
        Duration maxReservationDuration,
        RoomOperationStatus operationStatus,
        LocalTime openAt,
        LocalTime closeAt
) {
    public static RoomOperationPolicyResult from(RoomOperationPolicy operationPolicy) {
        return new RoomOperationPolicyResult(
                operationPolicy.getMaxReservationPerUser(),
                operationPolicy.getMaxReservationDuration(),
                operationPolicy.getOperationStatus(),
                operationPolicy.getOperationHours().getOpenAt(),
                operationPolicy.getOperationHours().getCloseAt()
        );
    }
}
