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
            return Stream.of(
                    arguments("roomId = null", (Supplier<Seat>) () -> new SeatFixture.Builder().roomId(null).build(), "roomId"),
                    arguments("code = null", (Supplier<Seat>) () -> new SeatFixture.Builder().code(null).build(), "code"),
                    arguments("createdAt = null", (Supplier<Seat>) () -> new SeatFixture.Builder().createdAt(null).build(), "createdAt")
            );
        }

        @Test
        @DisplayName("roomId과 code를 넘겨서 Seat 생성 가능")
        void create_with_room_and_code() {
            var roomId = RoomFixture.nextId();
            var code = "seat-A";
            var seat = Seat.of(roomId, code, now);

            assertThat(seat.getCode()).isEqualTo(code);
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
            var roomId = RoomFixture.nextId();
            var code = "seat-A";
            var seat = Seat.of(roomId, code, now);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.ACTIVE);
            assertThat(seat.getCreatedAt()).isEqualTo(seat.getLastActivatedAt());
        }

        @Test
        @DisplayName("INACTIVE 상태로 생성하면 lastInactivatedAt은 createdAt과 같다")
        void inactive_when_created() {
            var roomId = RoomFixture.nextId();
            var code = "seat-A";
            var seat = Seat.of(roomId, code, SeatStatus.INACTIVE, now);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);
            assertThat(seat.getCreatedAt()).isEqualTo(seat.getLastInactivatedAt());
        }

        @ParameterizedTest(name = "code = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("code가 공백이면 예외")
        void throw_exception_when_empty_code(String code) {
            var roomId = RoomFixture.nextId();

            assertThatDomainThrownBy(() -> Seat.of(roomId, code, now))
                    .hasNonBlankMessageFor("code");
        }
    }

    @Nested
    @DisplayName("변경 테스트")
    class UpdateTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("code = null", (Consumer<Seat>) (seat) -> seat.updateCode(null), "code"),
                    arguments("activatedAt = null", (Consumer<Seat>) (seat) -> seat.active(null), "activatedAt"),
                    arguments("inactivatedAt = null", (Consumer<Seat>) (seat) -> seat.inactive(null), "inactivatedAt")
            );
        }

        @Test
        @DisplayName("새로운 code로 변경 가능")
        void update_with_new_code() {
            var seat = SeatFixture.next();
            var newCode = "new " + seat.getCode();

            seat.updateCode(newCode);

            assertThat(seat.getCode()).isEqualTo(newCode);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("null 인자로 변경 시 예외")
        void throw_exception_when_update_with_null(
                String displayName,
                Consumer<Seat> consumer,
                String fieldName
        ) {
            var seat = SeatFixture.next();
            assertThatDomainThrownBy(() -> consumer.accept(seat))
                    .hasNonNullMessageFor(fieldName);
        }

        @ParameterizedTest(name = "newCode = {0}")
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("code가 공백이면 예외, 값은 안바뀐다.")
        void throw_exception_when_update_with_empty_code(String newCode) {
            var seat = SeatFixture.next();
            var code = seat.getCode();

            assertThatDomainThrownBy(() -> seat.updateCode(newCode))
                    .hasNonBlankMessageFor("code");

            assertThat(seat.getCode()).isEqualTo(code);
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