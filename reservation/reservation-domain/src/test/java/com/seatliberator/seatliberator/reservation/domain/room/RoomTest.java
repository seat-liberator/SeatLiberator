package com.seatliberator.seatliberator.reservation.domain.room;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.room.RoomFixture.OPERATION_POLICY;
import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Room 도메인 테스트")
public class RoomTest {

    Instant now = fixedClock.instant();

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("roomId = null", (Supplier<Room>) () -> new RoomFixture.Builder().roomId(null).build(), "roomId"),
                    arguments("createdAt = null", (Supplier<Room>) () -> new RoomFixture.Builder().createdAt(null).build(), "createdAt"),
                    arguments("operationPolicy = null", (Supplier<Room>) () -> new RoomFixture.Builder().operationPolicy(null).build(), "operationPolicy")
            );
        }

        @ParameterizedTest(name = "roomId = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("공백 roomId 전달하면 예외")
        void throw_exception_when_invalid_name(String roomId) {
            assertThatDomainThrownBy(() -> Room.of(roomId, OPERATION_POLICY, now))
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
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("roomId = null", (Consumer<Room>) (room) -> room.updateRoomId(null), "roomId"),
                    arguments("operationPolicy = null", (Consumer<Room>) (room) -> room.updateOperationPolicy(null), "operationPolicy")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("null 인자로 변경 시 예외")
        void throw_exception_when_update_with_null(
                String displayName,
                Consumer<Room> consumer,
                String fieldName
        ) {
            var room = RoomFixture.get();
            assertThatDomainThrownBy(() -> consumer.accept(room))
                    .hasNonNullMessageFor(fieldName);
        }

        @Test
        @DisplayName("방 ID를 변경할 수 있다")
        void update_room_id() {
            var room = RoomFixture.get();

            var newRoomId = "new-room-1";
            room.updateRoomId(newRoomId);

            assertThat(room.getRoomId()).isEqualTo(newRoomId);
        }

        @Test
        @DisplayName("방 운영 정책을 변경할 수 있다")
        void update_operation_policy() {
            var room = RoomFixture.get();
            var policy = new RoomOperationPolicyFixture.Builder()
                    .maxReservationPerUser(10)
                    .build();

            room.updateOperationPolicy(policy);

            assertThat(room.getOperationPolicy()).isSameAs(policy);

        }

        @ParameterizedTest(name = "newRoomId = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("roomId가 공백이면 예외, 값은 안바뀐다")
        void throw_exception_when_update_with_empty_roomId(String newRoomId) {
            var room = RoomFixture.get();
            var roomId = room.getRoomId();

            assertThatDomainThrownBy(() -> room.updateRoomId(newRoomId))
                    .hasNonBlankMessageFor("roomId");

            assertThat(room.getRoomId()).isEqualTo(roomId);
        }

        @ParameterizedTest(name = "newRoomId = {0}")
        @NullSource
        @DisplayName("roomId가 null이면 예외, 값은 안바뀐다")
        void throw_exception_when_update_with_null_roomId(String newRoomId) {
            var room = RoomFixture.get();
            var roomId = room.getRoomId();

            assertThatDomainThrownBy(() -> room.updateRoomId(newRoomId))
                    .hasNonNullMessageFor("roomId");

            assertThat(room.getRoomId()).isEqualTo(roomId);
        }
    }
}