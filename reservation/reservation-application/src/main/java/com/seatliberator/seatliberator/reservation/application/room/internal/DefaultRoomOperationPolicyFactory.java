package com.seatliberator.seatliberator.reservation.application.room.internal;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.shared.EmbeddableDailyTimeSegment;
import org.springframework.stereotype.Component;

@Component
public class DefaultRoomOperationPolicyFactory implements RoomOperationPolicyFactory {
    @Override
    public RoomOperationPolicy create(RoomOperationPolicyFactoryCommand command) {
        var operationHours = EmbeddableDailyTimeSegment.of(
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
