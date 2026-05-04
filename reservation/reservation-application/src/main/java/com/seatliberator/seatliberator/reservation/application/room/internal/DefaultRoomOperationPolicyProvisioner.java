package com.seatliberator.seatliberator.reservation.application.room.internal;

import com.seatliberator.seatliberator.reservation.domain.room.OperationHours;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Component
public class DefaultRoomOperationPolicyProvisioner implements RoomOperationPolicyProvisioner {
    @Override
    public RoomOperationPolicy provide() {
        return RoomOperationPolicy.of(
                5,
                Duration.ofHours(4),
                RoomOperationStatus.OPEN,
                OperationHours.of(LocalTime.of(6, 0), LocalTime.of(0, 0))
        );
    }
}
