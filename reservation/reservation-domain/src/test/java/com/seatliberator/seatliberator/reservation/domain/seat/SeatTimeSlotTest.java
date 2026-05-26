package com.seatliberator.seatliberator.reservation.domain.seat;

import com.seatliberator.seatliberator.reservation.domain.shared.ActiveInactiveTransitionContractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("SeatTimeSlot 도메인 테스트")
public class SeatTimeSlotTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("seatId = null", (Supplier<SeatTimeSlot>) () -> new SeatTimeSlotFixture.Builder().seatId(null).build(), "seatId"),
                    arguments("slotRange = null", (Supplier<SeatTimeSlot>) () -> new SeatTimeSlotFixture.Builder().slotRange(null).build(), "slotRange"),
                    arguments("slotStatus = null", (Supplier<SeatTimeSlot>) () -> new SeatTimeSlotFixture.Builder().slotStatus(null).build(), "slotStatus"),
                    arguments("createdAt = null", (Supplier<SeatTimeSlot>) () -> new SeatTimeSlotFixture.Builder().createdAt(null).build(), "createdAt")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_required_argument_is_null(
                String displayName,
                Supplier<SeatTimeSlot> supplier,
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
                    arguments("updateSlotRange = null", (Consumer<SeatTimeSlot>) (slot) -> slot.updateSlotRange(null), "slotRange")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("null 인자로 변경 시 예외")
        void throw_exception_when_update_with_null(
                String displayName,
                Consumer<SeatTimeSlot> consumer,
                String fieldName
        ) {
            var slot = SeatTimeSlotFixture.get();
            assertThatDomainThrownBy(() -> consumer.accept(slot))
                    .hasNonNullMessageFor(fieldName);
        }
    }

    @Nested
    @DisplayName("상태 전이 테스트")
    class TransitionTest implements ActiveInactiveTransitionContractTest<SeatTimeSlot> {

        @Override
        public SeatTimeSlot createActive(Instant createdAt) {
            return new SeatTimeSlotFixture.Builder().createdAt(createdAt).slotStatus(SeatTimeSlotStatus.ACTIVE).build();
        }

        @Override
        public SeatTimeSlot createInactive(Instant createdAt) {
            return new SeatTimeSlotFixture.Builder().createdAt(createdAt).slotStatus(SeatTimeSlotStatus.INACTIVE).build();
        }

        @Override
        public SeatTimeSlot activate(SeatTimeSlot domain, Instant activatedAt) {
            domain.active(activatedAt);
            return domain;
        }

        @Override
        public SeatTimeSlot inactivate(SeatTimeSlot domain, Instant inactivatedAt) {
            domain.inactive(inactivatedAt);
            return domain;
        }

        @Override
        public boolean isActive(SeatTimeSlot domain) {
            return domain.getSlotStatus() == SeatTimeSlotStatus.ACTIVE;
        }

        @Override
        public Instant getCreatedAt(SeatTimeSlot domain) {
            return domain.getCreatedAt();
        }

        @Override
        public Instant getLastActivatedAt(SeatTimeSlot domain) {
            return domain.getLastActivatedAt();
        }

        @Override
        public Instant getLastInactivatedAt(SeatTimeSlot domain) {
            return domain.getLastInactivatedAt();
        }
    }
}
