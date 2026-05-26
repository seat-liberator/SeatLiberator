package com.seatliberator.seatliberator.reservation.persistence.room;

import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RoomTestSupport {
    public static final UUID UNKNOWN_ROOM_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final String ROOM_CODE = "study-room-a";
    public static final String OTHER_ROOM_CODE = "study-room-b";
    public static final String UNKNOWN_ROOM_CODE = "study-room-z";

    private RoomTestSupport() {
    }

    public static Room room() {
        return room(ROOM_CODE);
    }

    public static Room otherRoom() {
        return room(OTHER_ROOM_CODE);
    }

    public static Room room(String code) {
        return new RoomFixture.Builder()
                .code(code)
                .operationPolicy(new RoomOperationPolicyFixture.Builder().build())
                .build();
    }

    public static void assertSameRoom(Room actual, Room expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getCode()).isEqualTo(expected.getCode());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.getOperationPolicy())
                .usingRecursiveComparison()
                .isEqualTo(expected.getOperationPolicy());
    }
}
