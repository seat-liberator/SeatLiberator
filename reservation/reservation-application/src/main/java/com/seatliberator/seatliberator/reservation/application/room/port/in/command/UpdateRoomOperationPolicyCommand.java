package com.seatliberator.seatliberator.reservation.application.room.port.in.command;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailySchedule;

import java.time.Duration;

public record UpdateRoomOperationPolicyCommand(
        String roomId,
        Integer maxReservationPerUser,
        Duration maxReservationDuration,
        RoomOperationStatus operationStatus,
        DailySchedule operationSchedule
) {
}
