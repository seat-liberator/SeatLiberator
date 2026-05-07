package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegmentFixtures.DURATION;
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegmentFixtures.START_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("EmbeddableDailyTimeSegment 도메인 테스트")
public class EmbeddableDailyTimeSegmentTest implements DailyTimeSegmentContractTest<EmbeddableDailyTimeSegment> {
    @Override
    public EmbeddableDailyTimeSegment create(long startNanoOfDay, long endNanoOfDay) {
        return EmbeddableDailyTimeSegment.of(startNanoOfDay, endNanoOfDay);
    }

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("startNanoOfDay = null", (Supplier<EmbeddableDailyTimeSegment>) () -> EmbeddableDailyTimeSegment.of(null, START_AT.toNanoOfDay() + DURATION.toNanos()), "startNanoOfDay"),
                    arguments("endNanoOfDay = null", (Supplier<EmbeddableDailyTimeSegment>) () -> EmbeddableDailyTimeSegment.of(START_AT.toNanoOfDay(), null), "endNanoOfDay"),
                    arguments("startAt = null", (Supplier<EmbeddableDailyTimeSegment>) () -> EmbeddableDailyTimeSegment.of(null, DURATION), "startAt"),
                    arguments("duration = null", (Supplier<EmbeddableDailyTimeSegment>) () -> EmbeddableDailyTimeSegment.of(START_AT, (Duration) null), "duration")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_required_argument_is_null(
                String displayName,
                Supplier<EmbeddableDailyTimeSegment> supplier,
                String fieldName
        ) {
            assertThatDomainThrownBy(supplier::get)
                    .hasNonNullMessageFor(fieldName);
        }

        @Test
        @DisplayName("다른 DailyTimeSegment로부터 동일한 구간을 복사할 수 있다")
        void copy_segment_with_from_factory() {
            var source = SimpleDailyTimeSegment.of(START_AT, DURATION);

            var copied = EmbeddableDailyTimeSegment.from(source);

            assertThat(copied.startNanoOfDay()).isEqualTo(START_AT.toNanoOfDay());
            assertThat(copied.endNanoOfDay()).isEqualTo(START_AT.toNanoOfDay() + DURATION.toNanos());
            assertThat(copied.duration()).isEqualTo(DURATION);
        }

        @Test
        @DisplayName("from 팩토리 인자가 null이면 예외")
        void throw_exception_when_from_factory_argument_is_null() {
            assertThatDomainThrownBy(() -> EmbeddableDailyTimeSegment.from(null))
                    .hasNonNullMessageFor("segment");
        }
    }

    @Nested
    @DisplayName("변경 테스트")
    class UpdateTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("startNanoOfDay = null", (Consumer<EmbeddableDailyTimeSegment>) (window) -> window.updateStartNanoOfDay(null), "startNanoOfDay"),
                    arguments("endNanoOfDay = null", (Consumer<EmbeddableDailyTimeSegment>) (window) -> window.updateEndNanoOfDay(null), "endNanoOfDay"),
                    arguments("startAt = null", (Consumer<EmbeddableDailyTimeSegment>) (window) -> window.updateStartAt(null), "startAt"),
                    arguments("duration = null", (Consumer<EmbeddableDailyTimeSegment>) (window) -> window.updateDuration(null), "duration"),
                    arguments("segment = null", (Consumer<EmbeddableDailyTimeSegment>) (window) -> window.apply(null), "segment")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("null 인자로 변경 시 예외")
        void throw_exception_when_update_with_null(
                String displayName,
                Consumer<EmbeddableDailyTimeSegment> consumer,
                String fieldName
        ) {
            var window = EmbeddableDailyTimeSegment.of(START_AT, DURATION);
            assertThatDomainThrownBy(() -> consumer.accept(window))
                    .hasNonNullMessageFor(fieldName);
        }

        @Test
        @DisplayName("시작 나노를 변경할 수 있다")
        void update_startNanoOfDay() {
            var window = EmbeddableDailyTimeSegment.of(START_AT, DURATION);
            var startNanoOfDay = LocalTime.of(8, 0).toNanoOfDay();

            window.updateStartNanoOfDay(startNanoOfDay);

            assertThat(window.startNanoOfDay()).isEqualTo(startNanoOfDay);
        }

        @Test
        @DisplayName("종료 나노를 변경할 수 있다")
        void update_endNanoOfDay() {
            var window = EmbeddableDailyTimeSegment.of(START_AT, DURATION);
            var endNanoOfDay = LocalTime.of(16, 0).toNanoOfDay();

            window.updateEndNanoOfDay(endNanoOfDay);

            assertThat(window.endNanoOfDay()).isEqualTo(endNanoOfDay);
        }

        @Test
        @DisplayName("시작 시간을 변경할 수 있다")
        void update_startAt() {
            var window = EmbeddableDailyTimeSegment.of(START_AT, DURATION);
            var startAt = LocalTime.of(8, 0);

            window.updateStartAt(startAt);

            assertThat(window.startNanoOfDay()).isEqualTo(startAt.toNanoOfDay());
        }

        @Test
        @DisplayName("지속 시간을 변경할 수 있다")
        void update_duration() {
            var window = EmbeddableDailyTimeSegment.of(START_AT, DURATION);
            var duration = Duration.ofHours(4);

            window.updateDuration(duration);

            assertThat(window.duration()).isEqualTo(duration);
        }

        @Test
        @DisplayName("다른 DailyTimeSegment 값으로 변경할 수 있다")
        void apply_segment() {
            var window = EmbeddableDailyTimeSegment.of(START_AT, DURATION);
            var other = SimpleDailyTimeSegment.of(LocalTime.of(9, 0), Duration.ofHours(2));

            window.apply(other);

            assertThat(window.isSame(other)).isTrue();
        }
    }
}
