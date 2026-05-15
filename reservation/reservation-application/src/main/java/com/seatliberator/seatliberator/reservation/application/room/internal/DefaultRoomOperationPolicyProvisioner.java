package com.seatliberator.seatliberator.reservation.application.room.internal;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailySchedule;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailySchedule;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Component
public class DefaultRoomOperationPolicyProvisioner implements RoomOperationPolicyProvisioner {
    @Override
    public RoomOperationPolicy provide() {
        return RoomOperationPolicy.of(
                5,
                Duration.ofHours(4),
                RoomOperationStatus.OPEN,
                getDefaultOperationSchedule()
        );
    }

    private DailySchedule getDefaultOperationSchedule() {
        return SimpleDailySchedule.of(
                List.of(
                        SimpleDailyNanoRange.of(LocalTime.of(6, 0), Duration.ofHours(6)),
                        SimpleDailyNanoRange.of(LocalTime.of(13, 0), Duration.ofHours(11)),
                        SimpleDailyNanoRange.of(LocalTime.of(0, 0), Duration.ofHours(3))
                )
        );
    }
}
