package com.seatliberator.seatliberator.reservation.application.room.internal;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.EmbeddableDailyTimeWindow;
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
                EmbeddableDailyTimeWindow.of(LocalTime.of(6, 0), LocalTime.of(0, 0))
        );
    }
}
