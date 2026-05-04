package com.seatliberator.seatliberator.reservation.application.room.internal;

import com.seatliberator.seatliberator.reservation.domain.room.OperationHours;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import org.springframework.stereotype.Component;

@Component
public class DefaultRoomOperationPolicyFactory implements RoomOperationPolicyFactory {
    @Override
    public RoomOperationPolicy create(RoomOperationPolicyFactoryCommand command) {
        var operationHours = OperationHours.of(
                command.openAt(),
                command.closeAt()
        );
        return RoomOperationPolicy.of(
                command.maxReservationPerUser(),
                command.maxReservationDuration(),
                command.operationStatus(),
                operationHours
        );
    }
}
