package com.seatliberator.seatliberator.reservation.application.room.internal;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomOperationPolicyCommand;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;

import java.time.Duration;
import java.time.LocalTime;

public record RoomOperationPolicyFactoryCommand(
        Integer maxReservationPerUser,
        Duration maxReservationDuration,
        RoomOperationStatus operationStatus,
        LocalTime openAt,
        LocalTime closeAt
) {
    public static RoomOperationPolicyFactoryCommand from(UpdateRoomOperationPolicyCommand command) {
        return new RoomOperationPolicyFactoryCommand(
                command.maxReservationPerUser(),
                command.maxReservationDuration(),
                command.operationStatus(),
                command.operationOpenAt(),
                command.operationCloseAt()
        );
    }
}
