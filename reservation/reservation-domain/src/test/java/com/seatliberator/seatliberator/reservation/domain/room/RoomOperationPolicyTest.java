package com.seatliberator.seatliberator.reservation.domain.room;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("RoomOperationPolicy 도메인 테스트")
public class RoomOperationPolicyTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("maxReservationPerUser = null", (Supplier<RoomOperationPolicy>) () -> new RoomOperationPolicyFixture.Builder().maxReservationPerUser(null).build(), "maxReservationPerUser"),
                    arguments("maxReservationDuration = null", (Supplier<RoomOperationPolicy>) () -> new RoomOperationPolicyFixture.Builder().maxReservationDuration(null).build(), "maxReservationDuration"),
                    arguments("operationStatus = null", (Supplier<RoomOperationPolicy>) () -> new RoomOperationPolicyFixture.Builder().operationStatus(null).build(), "operationStatus"),
                    arguments("operationHours = null", (Supplier<RoomOperationPolicy>) () -> new RoomOperationPolicyFixture.Builder().operationHours(null).build(), "operationHours")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_required_argument_is_null(
                String displayName,
                Supplier<RoomOperationPolicy> supplier,
                String fieldName
        ) {
            assertThatDomainThrownBy(supplier::get)
                    .hasNonNullMessageFor(fieldName);
        }

        @ParameterizedTest(name = "maxReservationPerUser = {0}")
        @ValueSource(ints = {0, -2, -1})
        @DisplayName("maxReservationPerUser가 음수 또는 0이면 예외")
        void throw_exception_when_maxReservationPerUser_is_negative(int maxReservationPerUser) {
            assertThatDomainThrownBy(() -> new RoomOperationPolicyFixture.Builder().maxReservationPerUser(maxReservationPerUser).build())
                    .hasPositiveMessageFor("maxReservationPerUser");
        }

        static Stream<Arguments> nonPositiveDurationCases() {
            return Stream.of(
                    arguments("maxReservationDuration = zero", Duration.ZERO),
                    arguments("maxReservationDuration = negative", Duration.ofMinutes(-1))
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nonPositiveDurationCases")
        @DisplayName("maxReservationDuration이 0 또는 음수이면 예외")
        void throw_exception_when_maxReservationDuration_is_not_positive(
                String displayName,
                Duration maxReservationDuration
        ) {
            assertThatDomainThrownBy(() -> new RoomOperationPolicyFixture.Builder().maxReservationDuration(maxReservationDuration).build())
                    .hasPositiveMessageFor("maxReservationDuration");
        }
    }

    @Nested
    @DisplayName("변경 테스트")
    class UpdateTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("maxReservationPerUser = null", (Consumer<RoomOperationPolicy>) (policy) -> policy.updateMaxReservationPerUser(null), "maxReservationPerUser"),
                    arguments("maxReservationDuration = null", (Consumer<RoomOperationPolicy>) (policy) -> policy.updateMaxReservationDuration(null), "maxReservationDuration"),
                    arguments("operationStatus = null", (Consumer<RoomOperationPolicy>) (policy) -> policy.updateOperationStatus(null), "operationStatus"),
                    arguments("operationHours = null", (Consumer<RoomOperationPolicy>) (policy) -> policy.updateOperationHours(null), "operationHours")
            );
        }

        static Stream<Arguments> nonPositiveDurationCases() {
            return Stream.of(
                    arguments("maxReservationDuration = zero", Duration.ZERO),
                    arguments("maxReservationDuration = negative", Duration.ofMinutes(-1))
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("null 인자로 변경 시 예외")
        void throw_exception_when_update_with_null(
                String displayName,
                Consumer<RoomOperationPolicy> consumer,
                String fieldName
        ) {
            var policy = RoomOperationPolicyFixture.get();
            assertThatDomainThrownBy(() -> consumer.accept(policy))
                    .hasNonNullMessageFor(fieldName);
        }

        @ParameterizedTest(name = "maxReservationPerUser = {0}")
        @ValueSource(ints = {0, -2, -1})
        @DisplayName("maxReservationPerUser를 0 또는 음수로 변경하면 예외")
        void throw_exception_when_update_maxReservationPerUser_when_non_positive_value(int maxReservationPerUser) {
            var policy = RoomOperationPolicyFixture.get();

            assertThatDomainThrownBy(() -> policy.updateMaxReservationPerUser(maxReservationPerUser))
                    .hasPositiveMessageFor("maxReservationPerUser");
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nonPositiveDurationCases")
        @DisplayName("maxReservationDuration을 0 또는 음수로 변경하면 예외")
        void throw_exception_when_update_maxReservationDuration_with_non_positive_value(
                String displayName,
                Duration maxReservationDuration
        ) {
            var policy = RoomOperationPolicyFixture.get();

            assertThatDomainThrownBy(() -> policy.updateMaxReservationDuration(maxReservationDuration))
                    .hasPositiveMessageFor("maxReservationDuration");
        }
    }
}
