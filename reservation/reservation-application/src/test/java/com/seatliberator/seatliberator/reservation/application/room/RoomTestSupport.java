package com.seatliberator.seatliberator.reservation.application.room;

import com.seatliberator.seatliberator.reservation.application.DefaultTestSupport;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.DeleteRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomCodeCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomOperationPolicyCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.query.FindRoomQuery;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailySchedule;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRangeFixture.AFTERNOON_RANGE;
import static com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRangeFixture.DAWN_RANGE;
import static org.assertj.core.api.Assertions.assertThat;

public class RoomTestSupport extends DefaultTestSupport {
    public static final UUID ROOM_ID = RoomFixture.nextId();
    public static final String ROOM_CODE = "study-room-a";
    public static final String NEW_ROOM_CODE = "study-room-b";

    public static CreateRoomCommand createRoomCommand() {
        return CreateRoomCommand.of(ROOM_CODE);
    }

    public static UpdateRoomCodeCommand updateRoomCodeCommand() {
        return UpdateRoomCodeCommand.of(ROOM_ID, NEW_ROOM_CODE);
    }

    public static DeleteRoomCommand deleteRoomCommand() {
        return DeleteRoomCommand.of(ROOM_ID);
    }

    public static FindRoomQuery findRoomQuery() {
        return FindRoomQuery.of(ROOM_ID);
    }

    public static Room room() {
        return room(ROOM_ID, ROOM_CODE);
    }

    public static Room room(UUID roomId, String code) {
        var room = new RoomFixture.Builder()
                .code(code)
                .createdAt(NOW)
                .build();
        setField(room, "id", roomId);
        return room;
    }

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

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("테스트용 필드 설정 실패: " + fieldName, e);
        }
    }
}
