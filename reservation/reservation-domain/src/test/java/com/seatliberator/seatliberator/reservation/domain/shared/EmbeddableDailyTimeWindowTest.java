package com.seatliberator.seatliberator.reservation.domain.shared;

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
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeWindowFixture.END_AT;
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeWindowFixture.START_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("EmbeddableDailyTimeWindow 도메인 테스트")
public class EmbeddableDailyTimeWindowTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("startAt = null", (Supplier<EmbeddableDailyTimeWindow>) () -> EmbeddableDailyTimeWindow.of(null, END_AT), "startAt"),
                    arguments("endAt = null", (Supplier<EmbeddableDailyTimeWindow>) () -> EmbeddableDailyTimeWindow.of(START_AT, null), "endAt")
            );
        }

        @Test
        @DisplayName("시작 및 종료 시각으로 생성한다")
        void create_with_openAt_and_closeAt() {
            var dailyTimeWindow = EmbeddableDailyTimeWindow.of(START_AT, END_AT);

            assertThat(dailyTimeWindow.startAt()).isEqualTo(START_AT);
            assertThat(dailyTimeWindow.endAt()).isEqualTo(END_AT);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_required_argument_is_null(
                String displayName,
                Supplier<EmbeddableDailyTimeWindow> supplier,
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
                    arguments("startAt = null", (Consumer<EmbeddableDailyTimeWindow>) (window) -> window.updateStartAt(null), "startAt"),
                    arguments("endAt = null", (Consumer<EmbeddableDailyTimeWindow>) (window) -> window.updateEndAt(null), "endAt")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("null 인자로 변경 시 예외")
        void throw_exception_when_update_with_null(
                String displayName,
                Consumer<EmbeddableDailyTimeWindow> consumer,
                String fieldName
        ) {
            var window = EmbeddableDailyTimeWindow.of(START_AT, END_AT);
            assertThatDomainThrownBy(() -> consumer.accept(window))
                    .hasNonNullMessageFor(fieldName);
        }

        @Test
        @DisplayName("운영 시작 시간을 변경할 수 있다")
        void update_open_at() {
            var window = EmbeddableDailyTimeWindow.of(START_AT, END_AT);
            var startAt = LocalTime.of(8, 0);

            window.updateStartAt(startAt);

            assertThat(window.getStartAt()).isEqualTo(startAt);
        }

        @Test
        @DisplayName("운영 종료 시간을 변경할 수 있다")
        void update_close_at() {
            var window = EmbeddableDailyTimeWindow.of(START_AT, END_AT);
            var endAt = LocalTime.of(22, 0);

            window.updateEndAt(endAt);

            assertThat(window.getEndAt()).isEqualTo(endAt);
        }
    }
}
