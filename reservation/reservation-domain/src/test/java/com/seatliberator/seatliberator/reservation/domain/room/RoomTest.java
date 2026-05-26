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
                    arguments("code = null", (Supplier<Room>) () -> new RoomFixture.Builder().code(null).build(), "code"),
                    arguments("createdAt = null", (Supplier<Room>) () -> new RoomFixture.Builder().createdAt(null).build(), "createdAt"),
                    arguments("operationPolicy = null", (Supplier<Room>) () -> new RoomFixture.Builder().operationPolicy(null).build(), "operationPolicy")
            );
        }

        @ParameterizedTest(name = "code = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("공백 code 전달하면 예외")
        void throw_exception_when_invalid_code(String code) {
            assertThatDomainThrownBy(() -> Room.of(code, RoomOperationPolicyFixture.get(), now))
                    .hasNonBlankMessageFor("code");
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
                    arguments("code = null", (Consumer<Room>) (room) -> room.updateCode(null), "code"),
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
            var room = RoomFixture.next();
            assertThatDomainThrownBy(() -> consumer.accept(room))
                    .hasNonNullMessageFor(fieldName);
        }

        @Test
        @DisplayName("방 ID를 변경할 수 있다")
        void update_room_id() {
            var room = RoomFixture.next();

            var newRoomId = "new-room-1";
            room.updateCode(newRoomId);

            assertThat(room.getCode()).isEqualTo(newRoomId);
        }

        @Test
        @DisplayName("방 운영 정책을 변경할 수 있다")
        void update_operation_policy() {
            var room = RoomFixture.next();
            var policy = new RoomOperationPolicyFixture.Builder()
                    .maxReservationPerUser(10)
                    .build();

            room.updateOperationPolicy(policy);

            assertThat(room.getOperationPolicy()).isSameAs(policy);

        }

        @ParameterizedTest(name = "newCode = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("code가 공백이면 예외, 값은 안바뀐다")
        void throw_exception_when_update_with_empty_code(String newCode) {
            var room = RoomFixture.next();
            var code = room.getCode();

            assertThatDomainThrownBy(() -> room.updateCode(newCode))
                    .hasNonBlankMessageFor("code");

            assertThat(room.getCode()).isEqualTo(code);
        }

        @ParameterizedTest(name = "newCode = {0}")
        @NullSource
        @DisplayName("code가 null이면 예외, 값은 안바뀐다")
        void throw_exception_when_update_with_null_code(String newCode) {
            var room = RoomFixture.next();
            var code = room.getCode();

            assertThatDomainThrownBy(() -> room.updateCode(newCode))
                    .hasNonNullMessageFor("code");

            assertThat(room.getCode()).isEqualTo(code);
        }
    }
}