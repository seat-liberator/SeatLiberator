package com.seatliberator.seatliberator.reservation.application.room.internal;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegment;
import com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegments;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleDailyTimeSegment;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleDailyTimeSegments;
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
                getDefaultOperationTimeSegments()
        );
    }

    private DailyTimeSegments getDefaultOperationTimeSegments() {
        return SimpleDailyTimeSegments.of(getDefaultOperationSchedule());
    }

    private List<DailyTimeSegment> getDefaultOperationSchedule() {
        return List.of(
                SimpleDailyTimeSegment.of(LocalTime.of(6, 0), Duration.ofHours(6)),
                SimpleDailyTimeSegment.of(LocalTime.of(13, 0), Duration.ofHours(11)),
                SimpleDailyTimeSegment.of(LocalTime.of(0, 0), Duration.ofHours(3))
        );
    }
}
