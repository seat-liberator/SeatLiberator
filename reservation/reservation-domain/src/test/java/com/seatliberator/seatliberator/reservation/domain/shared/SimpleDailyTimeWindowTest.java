package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeWindowFixture.END_AT;
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeWindowFixture.START_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("SimpleDailyTimeWindow 도메인 테스트")
public class SimpleDailyTimeWindowTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("startAt = null", (Supplier<SimpleDailyTimeWindow>) () -> SimpleDailyTimeWindow.of(null, END_AT), "startAt"),
                    arguments("endAt = null", (Supplier<SimpleDailyTimeWindow>) () -> SimpleDailyTimeWindow.of(START_AT, null), "endAt")
            );
        }

        @Test
        @DisplayName("시작 및 종료 시각으로 생성한다")
        void create_with_startAt_and_endAt() {
            var dailyTimeWindow = SimpleDailyTimeWindow.of(START_AT, END_AT);

            assertThat(dailyTimeWindow.startAt()).isEqualTo(START_AT);
            assertThat(dailyTimeWindow.endAt()).isEqualTo(END_AT);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_required_argument_is_null(
                String displayName,
                Supplier<SimpleDailyTimeWindow> supplier,
                String fieldName
        ) {
            assertThatDomainThrownBy(supplier::get)
                    .hasNonNullMessageFor(fieldName);
        }

        @Test
        @DisplayName("시작 시간과 종료 시간이 같으면 예외")
        void throw_exception_when_start_at_equals_end_at() {
            assertThatThrownBy(() -> SimpleDailyTimeWindow.of(START_AT, START_AT))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("startAt and endAt must not be same.");
        }

        @Test
        @DisplayName("다른 DailyTimeWindow로부터 동일한 구간을 복사할 수 있다")
        void copy_window_with_from_factory() {
            var source = SimpleDailyTimeWindow.of(START_AT, END_AT);

            var copied = SimpleDailyTimeWindow.from(source);

            assertThat(copied.startAt()).isEqualTo(START_AT);
            assertThat(copied.endAt()).isEqualTo(END_AT);
        }

        @Test
        @DisplayName("from 팩토리 인자가 null이면 예외")
        void throw_exception_when_from_factory_argument_is_null() {
            assertThatDomainThrownBy(() -> SimpleDailyTimeWindow.from(null))
                    .hasNonNullMessageFor("dailyTimeWindow");
        }
    }

    @Nested
    @DisplayName("Query 테스트")
    class QueryTest {
        @Test
        @DisplayName("시작 및 종료 시각이 같으면 동일한 구간이다")
        void return_true_when_startAt_and_endAt_are_same() {
            var window = SimpleDailyTimeWindow.of(START_AT, END_AT);
            var other = SimpleDailyTimeWindow.of(START_AT, END_AT);

            assertThat(window.isSame(other)).isTrue();
        }

        @Test
        @DisplayName("시작 또는 종료 시각이 다르면 다른 구간이다")
        void return_false_when_startAt_or_endAt_is_different() {
            var window = SimpleDailyTimeWindow.of(START_AT, END_AT);
            var other = SimpleDailyTimeWindow.of(START_AT.plusHours(1), END_AT);

            assertThat(window.isSame(other)).isFalse();
        }
    }
}
