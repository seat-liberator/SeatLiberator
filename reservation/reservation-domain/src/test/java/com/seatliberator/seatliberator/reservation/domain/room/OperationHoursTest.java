package com.seatliberator.seatliberator.reservation.domain.room;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("OperationHours 도메인 테스트")
public class OperationHoursTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("openAt = null", (Supplier<OperationHours>) () -> new OperationHourFixture.Builder().openAt(null).build(), "openAt"),
                    arguments("closeAt = null", (Supplier<OperationHours>) () -> new OperationHourFixture.Builder().closeAt(null).build(), "closeAt")
            );
        }

        @Test
        @DisplayName("운영 시작 시간과 종료 시간으로 생성할 수 있다")
        void create_with_openAt_and_closeAt() {
            var openAt = LocalTime.of(9, 0);
            var closeAt = LocalTime.of(18, 0);

            var operationHours = OperationHours.of(openAt, closeAt);

            assertThat(operationHours.getOpenAt()).isEqualTo(openAt);
            assertThat(operationHours.getCloseAt()).isEqualTo(closeAt);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_required_argument_is_null(
                String displayName,
                Supplier<OperationHours> supplier,
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
                    arguments("openAt = null", (Consumer<OperationHours>) (operationHours) -> operationHours.updateOpenAt(null), "openAt"),
                    arguments("closeAt = null", (Consumer<OperationHours>) (operationHours) -> operationHours.updateCloseAt(null), "closeAt")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("null 인자로 변경 시 예외")
        void throw_exception_when_update_with_null(
                String displayName,
                Consumer<OperationHours> consumer,
                String fieldName
        ) {
            var operationHours = OperationHourFixture.get();
            assertThatDomainThrownBy(() -> consumer.accept(operationHours))
                    .hasNonNullMessageFor(fieldName);
        }

        @Test
        @DisplayName("운영 시작 시간을 변경할 수 있다")
        void update_open_at() {
            var operationHours = OperationHourFixture.get();
            var openAt = LocalTime.of(8, 0);

            operationHours.updateOpenAt(openAt);

            assertThat(operationHours.getOpenAt()).isEqualTo(openAt);
        }

        @Test
        @DisplayName("운영 종료 시간을 변경할 수 있다")
        void update_close_at() {
            var operationHours = OperationHourFixture.get();
            var closeAt = LocalTime.of(22, 0);

            operationHours.updateCloseAt(closeAt);

            assertThat(operationHours.getCloseAt()).isEqualTo(closeAt);
        }
    }
}
