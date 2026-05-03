package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.SeatStatus;
import com.seatliberator.seatliberator.reservation.domain.fixture.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.fixture.SeatFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.fixture.RoomFixture.createRoom;
import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatFixture.*;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Seat 도메인 테스트")
public class SeatTest {

    Clock clock = fixedClock;

    Instant now = clock.instant();

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        @Test
        @DisplayName("room과 seatId를 넘겨서 Seat 생성 가능")
        void create_with_roomId_and_seatId() {
            var room = createRoom();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, now);

            assertThat(seat.getSeatId()).isEqualTo(seatId);
            assertThat(seat.getCreatedAt()).isEqualTo(now);
        }

        static Stream<Arguments> nullArgumentCases() {
            var room = createRoom();
            var seatId = INITIAL_SEAT_ID;
            var now = INITIAL_CREATED_AT;

            return Stream.of(
                    arguments("room = null", (Supplier<Seat>) () -> Seat.of(null, seatId, now), "room"),
                    arguments("seatId = null", (Supplier<Seat>) () -> Seat.of(room, null, now), "seatId"),
                    arguments("createdAt = null", (Supplier<Seat>) () -> Seat.of(room, seatId, null), "createdAt")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_required_argument_is_null(
                String displayName,
                Supplier<Seat> supplier,
                String fieldName
        ) {
            assertThatDomainThrownBy(supplier::get)
                    .hasNonNullMessageFor(fieldName);
        }

        @Test
        @DisplayName("초기 상태를 전달하지 않으면 ACTIVE로 초기화되고 lastActivatedAt은 createdAt과 같다")
        void active_when_created() {
            var room = createRoom();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, now);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.ACTIVE);
            assertThat(seat.getCreatedAt()).isEqualTo(seat.getLastActivatedAt());
        }

        @Test
        @DisplayName("INACTIVE 상태로 생성하면 lastInactivatedAt은 createdAt과 같다")
        void inactive_when_created() {
            var room = createRoom();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, SeatStatus.INACTIVE, now);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);
            assertThat(seat.getCreatedAt()).isEqualTo(seat.getLastInactivatedAt());
        }

        @ParameterizedTest(name = "seatId = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("seatId가 공백이면 예외")
        void throw_exception_when_empty_seatId(String seatId) {
            var room = createRoom();

            assertThatDomainThrownBy(() -> Seat.of(room, seatId, now))
                    .hasNonBlankMessageFor("seatId");
        }
    }

    @Nested
    @DisplayName("변경 테스트")
    class UpdateTest {
        @Test
        @DisplayName("새로운 seatId로 변경 가능")
        void update_with_new_seatId() {
            var room = createRoom();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, now);

            assertThat(seat.getSeatId()).isEqualTo(seatId);

            var newSeatId = "seat-B";
            seat.updateSeatId(newSeatId);

            assertThat(seat.getSeatId()).isEqualTo(newSeatId);
        }

        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("room = null", (Consumer<Seat>) (seat) -> seat.updateRoom(null), "room"),
                    arguments("seatId = null", (Consumer<Seat>) (seat) -> seat.updateSeatId(null), "seatId")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("null 인자로 변경 시 예외")
        void throw_exception_when_update_with_null(
                String displayName,
                Consumer<Seat> consumer,
                String fieldName
        ) {
            var seat = createSeat();
            assertThatDomainThrownBy(() -> consumer.accept(seat))
                    .hasNonNullMessageFor(fieldName);
        }

        @Test
        @DisplayName("새로운 방으로 변경 가능")
        void update_with_new_room() {
            var seatId = "seat-A";
            var oldRoomId = "study-room";
            var newRoomId = "new-study-room";

            var oldRoom = new RoomFixture.Builder()
                    .roomId(oldRoomId)
                    .build();

            var newRoom = new RoomFixture.Builder()
                    .roomId(newRoomId)
                    .build();

            var seat = new SeatFixture.Builder()
                    .seatId(seatId)
                    .room(oldRoom)
                    .build();

            assertThat(seat.getRoom()).isEqualTo(oldRoom);

            seat.updateRoom(newRoom);

            assertThat(seat.getRoom()).isEqualTo(newRoom);
        }

        @Test
        @DisplayName("같은 방으로 변경하면 기존 방이랑 동일")
        void ignore_when_update_same_room() {
            var seatId = "seat-A";
            var roomId = "room-1";
            var room = new RoomFixture.Builder().roomId(roomId).build();
            var seat = new SeatFixture.Builder().seatId(seatId).room(room).build();

            seat.updateRoom(room);

            assertThat(seat.getRoom()).isEqualTo(room);
        }

        @ParameterizedTest(name = "newSeatId = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("seatId가 공백이면 예외, 값은 안바뀐다.")
        void throw_exception_when_update_with_empty_seatId(String newSeatId) {
            var seat = createSeat();
            var seatId = seat.getSeatId();

            assertThatDomainThrownBy(() -> seat.updateSeatId(newSeatId))
                    .hasNonBlankMessageFor("seatId");

            assertThat(seat.getSeatId()).isEqualTo(seatId);
        }
    }

    @Nested
    @DisplayName("상태 전이 테스트")
    class TransitionTest {
        @Test
        @DisplayName("좌석을 비활성화 상태로 변경할 수 있다")
        void can_transition_inactive() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            var inactivatedAt = now.plusSeconds(5);
            seat.inactive(inactivatedAt);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);
        }

        @Test
        @DisplayName("비활성화된 좌석을 또 비활성화할 수 없다")
        void can_not_transition_to_inactive_from_inactive() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            assertThatThrownBy(() -> seat.inactive(now))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Can not transition to inactive from inactive");
        }

        @Test
        @DisplayName("비활성화된 좌석은 다시 활성화할 수 있다")
        void can_transition_to_active_from_inactive() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);

            var activatedAt = now.plusSeconds(5);
            seat.active(activatedAt);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.ACTIVE);
        }

        @Test
        @DisplayName("활성화된 좌석은 다시 활성화할 수 없다")
        void can_not_transition_to_active_from_active() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            assertThatThrownBy(() -> seat.active(now.plusSeconds(5)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Can not transition to active from active");
        }
    }

    @Nested
    @DisplayName("시간 불변식 테스트")
    class TimeInvariantTest {
        @Test
        @DisplayName("좌석 비활성화 시, 비활성화 시각이 null이면 예외")
        void throw_exception_when_inactive_with_null_inactivated_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            assertThatDomainThrownBy(() -> seat.inactive(null))
                    .hasNonNullMessageFor("inactivatedAt");
        }

        @Test
        @DisplayName("좌석 비활성화 시, 비활성화 시각이 생성 시각보다 과거면 예외")
        void throw_exception_when_inactivated_at_is_before_than_created_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            var inactivatedAt = now.minusSeconds(5);
            assertThatThrownBy(() -> seat.inactive(inactivatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("inactivatedAt can not be earlier than createdAt.");
        }

        @Test
        @DisplayName("좌석 비활성화 시, 비활성화 시각이 직전 활성화 시각보다 과거면 예외")
        void throw_exception_when_inactivated_at_is_before_than_last_activated_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            var activatedAt = now.plusSeconds(5);
            seat.active(activatedAt);

            var inactivatedAt = activatedAt.minusSeconds(2);
            assertThatThrownBy(() -> seat.inactive(inactivatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("inactivatedAt can not be earlier than lastActivatedAt.");
        }

        @Test
        @DisplayName("비활성화 시각이 생성 시각과 같으면 허용된다")
        void can_inactive_with_inactivatedAt_same_as_createdAt() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            seat.inactive(now);

            assertThat(seat.getLastInactivatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성화된 좌석은 activatedAt을 갖는다")
        void store_activated_at_when_active_seat() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);

            var activatedAt = now.plusSeconds(5);
            seat.active(activatedAt);

            assertThat(seat.getLastActivatedAt()).isEqualTo(activatedAt);
        }

        @Test
        @DisplayName("좌석 활성화 시, activatedAt이 null이면 예외")
        void throw_exception_when_active_with_null_activated_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);

            assertThatDomainThrownBy(() -> seat.active(null))
                    .hasNonNullMessageFor("activatedAt");
        }

        @Test
        @DisplayName("좌석 활성화 시, activatedAt이 createdAt보다 과거면 예외")
        void throw_exception_when_activated_at_is_before_than_created_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);

            var activatedAt = now.minusSeconds(5);
            assertThatThrownBy(() -> seat.active(activatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("activatedAt can not be earlier than createdAt.");
        }

        @Test
        @DisplayName("좌석 활성화 시, 활성화 시각이 직전 비활성화 시각보다 과거면 예외")
        void throw_exception_when_activated_at_is_before_than_last_inactivated_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            var inactivatedAt = now.plusSeconds(5);
            seat.inactive(inactivatedAt);

            var activatedAt = inactivatedAt.minusSeconds(2);
            assertThatThrownBy(() -> seat.active(activatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("activatedAt can not be earlier than lastInactivatedAt.");
        }

        @Test
        @DisplayName("활성화 시각이 생성 시각과 같으면 허용된다")
        void can_active_with_activatedAt_same_as_createdAt() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.ACTIVE);

            seat.inactive(now);
            seat.active(now);

            assertThat(seat.getLastActivatedAt()).isEqualTo(now);
        }
    }
}