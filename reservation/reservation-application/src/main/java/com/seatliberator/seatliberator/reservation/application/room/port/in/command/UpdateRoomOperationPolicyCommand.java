package com.seatliberator.seatliberator.reservation.application.room.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailySchedule;

import java.time.Duration;
import java.util.UUID;

public record UpdateRoomOperationPolicyCommand(
        UUID roomId,
        Integer maxReservationPerUser,
        Duration maxReservationDuration,
        RoomOperationStatus operationStatus,
        DailySchedule operationSchedule
) {
    public UpdateRoomOperationPolicyCommand {
        Preconditions.requireNonNull(roomId, "roomId");
        Preconditions.requirePositive(maxReservationDuration, "maxReservationDuration");
        Preconditions.requirePositive(maxReservationPerUser, "maxReservationPerUser");
        Preconditions.requireNonNull(operationStatus, "operationStatus");
        Preconditions.requireNonNull(operationSchedule, "operationSchedule");
    }

    public static UpdateRoomOperationPolicyCommand of(
            UUID roomId,
            Integer maxReservationPerUser,
            Duration maxReservationDuration,
            RoomOperationStatus operationStatus,
            DailySchedule operationSchedule
    ) {
        return new UpdateRoomOperationPolicyCommand(
                roomId,
                maxReservationPerUser,
                maxReservationDuration,
                operationStatus,
                operationSchedule
        );
    }
}
