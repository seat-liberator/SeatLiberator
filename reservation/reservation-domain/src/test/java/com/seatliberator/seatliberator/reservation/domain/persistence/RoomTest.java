package com.seatliberator.seatliberator.reservation.domain.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Room")
public class RoomTest {

    Instant now = fixedClock.instant();

    @Nested
    @DisplayName("Creation")
    class Creation {
        @Test
        @DisplayName("유효한 Id와 현재 시각으로 생성한다")
        void create_with_valid_name_and_created_at() {
            var roomId = "study-room-1";

            var room = Room.of(roomId, now);

            assertThat(room.getRoomId()).isEqualTo(roomId);
            assertThat(room.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("유효하지 않은 Id 전달하면 예외")
        void throw_exception_when_invalid_name() {
            var roomId = " ";

            assertThatThrownBy(() -> Room.of(roomId, now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("roomId must not be null or blank.");
        }

        @Test
        @DisplayName("유효하지 않은 현재 시각 전달하면 예외")
        void throw_exception_when_invalid_created_at() {
            var roomId = "study-room-1";

            assertThatThrownBy(() -> Room.of(roomId, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("createdAt must not be null.");
        }
    }

    @Nested
    @DisplayName("Update")
    class Update {
        @Test
        @DisplayName("방 ID를 변경할 수 있다")
        void update_room_id() {
            var roomId = "study-room-1";
            var room = Room.of(roomId, now);

            assertThat(room.getRoomId()).isEqualTo(roomId);

            var newRoomId = "new-room-1";
            room.updateRoomId(newRoomId);

            assertThat(room.getRoomId()).isEqualTo(newRoomId);
        }

        @Test
        @DisplayName("유효하지 않은 방 ID로 변경하면 예외")
        void throw_exception_when_update_with_invalid_roomId() {
            var roomId = "study-room-1";
            var room = Room.of(roomId, now);

            assertThat(room.getRoomId()).isEqualTo(roomId);

            assertThatThrownBy(() -> room.updateRoomId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("roomId must not be null or blank.");

            var newRoomId = " ";
            assertThatThrownBy(() -> room.updateRoomId(newRoomId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("roomId must not be null or blank.");

            assertThat(room.getRoomId()).isEqualTo(roomId);
        }


    }
}