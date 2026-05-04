package com.seatliberator.seatliberator.reservation.domain.seat;

import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.ActiveInactiveTransitionContractTest;
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
import static com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture.*;
import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Seat 도메인 테스트")
public class SeatTest {

    Clock clock = fixedClock;

    Instant now = clock.instant();

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        static Stream<Arguments> nullArgumentCases() {
            var room = RoomFixture.get();
            var seatId = INITIAL_SEAT_ID;
            var now = INITIAL_CREATED_AT;

            return Stream.of(
                    arguments("room = null", (Supplier<Seat>) () -> Seat.of(null, seatId, now), "room"),
                    arguments("seatId = null", (Supplier<Seat>) () -> Seat.of(room, null, now), "seatId"),
                    arguments("createdAt = null", (Supplier<Seat>) () -> Seat.of(room, seatId, null), "createdAt")
            );
        }

        @Test
        @DisplayName("room과 seatId를 넘겨서 Seat 생성 가능")
        void create_with_roomId_and_seatId() {
            var room = RoomFixture.get();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, now);

            assertThat(seat.getSeatId()).isEqualTo(seatId);
            assertThat(seat.getCreatedAt()).isEqualTo(now);
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
            var room = RoomFixture.get();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, now);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.ACTIVE);
            assertThat(seat.getCreatedAt()).isEqualTo(seat.getLastActivatedAt());
        }

        @Test
        @DisplayName("INACTIVE 상태로 생성하면 lastInactivatedAt은 createdAt과 같다")
        void inactive_when_created() {
            var room = RoomFixture.get();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, SeatStatus.INACTIVE, now);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);
            assertThat(seat.getCreatedAt()).isEqualTo(seat.getLastInactivatedAt());
        }

        @ParameterizedTest(name = "seatId = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("seatId가 공백이면 예외")
        void throw_exception_when_empty_seatId(String seatId) {
            var room = RoomFixture.get();

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
            var room = RoomFixture.get();
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
                    arguments("seatId = null", (Consumer<Seat>) (seat) -> seat.updateSeatId(null), "seatId"),
                    arguments("activatedAt = null", (Consumer<Seat>) (seat) -> seat.active(null), "activatedAt"),
                    arguments("inactivatedAt = null", (Consumer<Seat>) (seat) -> seat.inactive(null), "inactivatedAt")
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
    class TransitionTest implements ActiveInactiveTransitionContractTest<Seat> {
        @Override
        public Seat createActive(Instant createdAt) {
            return new SeatFixture.Builder().createdAt(createdAt).status(SeatStatus.ACTIVE).build();
        }

        @Override
        public Seat createInactive(Instant createdAt) {
            return new SeatFixture.Builder().createdAt(createdAt).status(SeatStatus.INACTIVE).build();
        }

        @Override
        public Seat activate(Seat domain, Instant activatedAt) {
            domain.active(activatedAt);
            return domain;
        }

        @Override
        public Seat inactivate(Seat domain, Instant inactivatedAt) {
            domain.inactive(inactivatedAt);
            return domain;
        }

        @Override
        public boolean isActive(Seat domain) {
            return domain.getStatus() == SeatStatus.ACTIVE;
        }

        @Override
        public Instant getCreatedAt(Seat domain) {
            return domain.getCreatedAt();
        }

        @Override
        public Instant getLastActivatedAt(Seat domain) {
            return domain.getLastActivatedAt();
        }

        @Override
        public Instant getLastInactivatedAt(Seat domain) {
            return domain.getLastInactivatedAt();
        }
    }
}