package com.seatliberator.seatliberator.reservation.application.room;

import com.seatliberator.seatliberator.reservation.application.AbstractTestSupport;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomOperationPolicyCommand;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleDailySchedule;

import java.time.Duration;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.shared.DailyNanoRangeFixture.AFTERNOON_RANGE;
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyNanoRangeFixture.DAWN_RANGE;
import static org.assertj.core.api.Assertions.assertThat;

public class RoomTestSupport extends AbstractTestSupport {
    public static UpdateRoomOperationPolicyCommand updateRoomOperationPolicyCommand() {
        return new UpdateRoomOperationPolicyCommand(
                ROOM_ID,
                4,
                Duration.ofHours(4),
                RoomOperationStatus.CLOSE,
                SimpleDailySchedule.of(List.of(
                        AFTERNOON_RANGE,
                        DAWN_RANGE
                ))
        );
    }

    public static RoomOperationPolicy updatedOperationPolicy() {
        return new RoomOperationPolicyFixture.Builder()
                .maxReservationPerUser(4)
                .maxReservationDuration(Duration.ofHours(4))
                .operationStatus(RoomOperationStatus.CLOSE)
                .operationSchedule(
                        SimpleDailySchedule.of(List.of(
                                AFTERNOON_RANGE,
                                DAWN_RANGE
                        ))
                )
                .build();
    }

    public static void assertEqualPolicy(RoomOperationPolicy actual, RoomOperationPolicy expect) {
        assertThat(actual.getMaxReservationPerUser()).isEqualTo(expect.getMaxReservationPerUser());
        assertThat(actual.getMaxReservationDuration()).isEqualByComparingTo(expect.getMaxReservationDuration());
        assertThat(actual.getOperationStatus()).isEqualTo(expect.getOperationStatus());

        var actualSchedule = SimpleDailySchedule.of(actual.getOperationSchedule());
        var expectSchedule = SimpleDailySchedule.of(expect.getOperationSchedule());
        assertThat(actualSchedule).isEqualTo(expectSchedule);
    }
}
