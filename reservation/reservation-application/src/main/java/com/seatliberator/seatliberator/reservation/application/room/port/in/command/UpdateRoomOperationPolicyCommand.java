package com.seatliberator.seatliberator.reservation.application.room.port.in.command;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;

import java.time.Duration;
import java.time.LocalTime;

public record UpdateRoomOperationPolicyCommand(
        String roomId,
        Integer maxReservationPerUser,
        Duration maxReservationDuration,
        RoomOperationStatus operationStatus,
        LocalTime operationOpenAt,
        LocalTime operationCloseAt
) {
}
