package com.seatliberator.seatliberator.reservation.domain.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.fixture.RoomFixture.INITIAL_ROOM_ID;
import static com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture.INITIAL_CREATED_AT;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Room 도메인 테스트")
public class RoomTest {

    Instant now = fixedClock.instant();

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        @Test
        @DisplayName("유효한 Id와 현재 시각으로 생성한다")
        void create_with_valid_name_and_created_at() {
            var roomId = "study-room-1";

            var room = Room.of(roomId, now);

            assertThat(room.getRoomId()).isEqualTo(roomId);
            assertThat(room.getCreatedAt()).isEqualTo(now);
        }

        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("roomId = null", (Supplier<Room>) () -> Room.of(null, INITIAL_CREATED_AT), "roomId"),
                    arguments("createdAt = null", (Supplier<Room>) () -> Room.of(INITIAL_ROOM_ID, null), "createdAt")
            );
        }

        @ParameterizedTest(name = "roomId = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("공백 roomId 전달하면 예외")
        void throw_exception_when_invalid_name(String roomId) {
            assertThatDomainThrownBy(() -> Room.of(roomId, now))
                    .hasNonBlankMessageFor("roomId");
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_required_argument_is_null(
                String displayName,
                Supplier<Room> supplier,
                String fieldName
        ) {
            assertThatDomainThrownBy(supplier::get)
                    .hasNonNullMessageFor(fieldName);
        }
    }

    @Nested
    @DisplayName("변경 테스트")
    class UpdateTest {
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

        @ParameterizedTest(name = "newRoomId = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("roomId가 공백이면 예외, 값은 안바뀐다")
        void throw_exception_when_update_with_empty_roomId(String newRoomId) {
            var roomId = "study-room-1";
            var room = Room.of(roomId, now);

            assertThatDomainThrownBy(() -> room.updateRoomId(newRoomId))
                    .hasNonBlankMessageFor("roomId");

            assertThat(room.getRoomId()).isEqualTo(roomId);
        }

        @ParameterizedTest(name = "newRoomId = {0}")
        @NullSource
        @DisplayName("roomId가 null이면 예외, 값은 안바뀐다")
        void throw_exception_when_update_with_null_roomId(String newRoomId) {
            var roomId = "study-room-1";
            var room = Room.of(roomId, now);

            assertThatDomainThrownBy(() -> room.updateRoomId(newRoomId))
                    .hasNonNullMessageFor("roomId");

            assertThat(room.getRoomId()).isEqualTo(roomId);
        }
    }
}