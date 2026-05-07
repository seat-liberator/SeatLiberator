package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegmentFixtures.DURATION;
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegmentFixtures.START_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("SimpleDailyTimeSegment 도메인 테스트")
public class SimpleDailyTimeSegmentTest implements DailyTimeSegmentContractTest<SimpleDailyTimeSegment> {

    @Override
    public SimpleDailyTimeSegment create(long startNanoOfDay, long endNanoOfDay) {
        return SimpleDailyTimeSegment.of(startNanoOfDay, endNanoOfDay);
    }

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("startNanoOfDay = null", (Supplier<SimpleDailyTimeSegment>) () -> SimpleDailyTimeSegment.of(null, START_AT.toNanoOfDay() + DURATION.toNanos()), "startNanoOfDay"),
                    arguments("endNanoOfDay = null", (Supplier<SimpleDailyTimeSegment>) () -> SimpleDailyTimeSegment.of(START_AT.toNanoOfDay(), null), "endNanoOfDay"),
                    arguments("startAt = null", (Supplier<SimpleDailyTimeSegment>) () -> SimpleDailyTimeSegment.of((LocalTime) null, DURATION), "startAt"),
                    arguments("duration = null", (Supplier<SimpleDailyTimeSegment>) () -> SimpleDailyTimeSegment.of(START_AT, null), "duration")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_required_argument_is_null(
                String displayName,
                Supplier<SimpleDailyTimeSegment> supplier,
                String fieldName
        ) {
            assertThatDomainThrownBy(supplier::get)
                    .hasNonNullMessageFor(fieldName);
        }

        @Test
        @DisplayName("다른 DailyTimeSegment로부터 동일한 구간을 복사할 수 있다")
        void copy_segment_with_from_factory() {
            var source = SimpleDailyTimeSegment.of(START_AT, DURATION);

            var copied = SimpleDailyTimeSegment.from(source);

            assertThat(copied.startNanoOfDay()).isEqualTo(START_AT.toNanoOfDay());
            assertThat(copied.endNanoOfDay()).isEqualTo(START_AT.toNanoOfDay() + DURATION.toNanos());
            assertThat(copied.duration()).isEqualTo(DURATION);
        }

        @Test
        @DisplayName("from 팩토리 인자가 null이면 예외")
        void throw_exception_when_from_factory_argument_is_null() {
            assertThatDomainThrownBy(() -> SimpleDailyTimeSegment.from(null))
                    .hasNonNullMessageFor("dailyTimeSegment");
        }
    }

    @Nested
    @DisplayName("편의 팩토리 테스트")
    class ConvenienceFactoryTest {
        @Test
        @DisplayName("시작 시간과 지속 시간으로 구간을 생성할 수 있다")
        void create_with_startAt_and_duration() {
            var segment = SimpleDailyTimeSegment.of(START_AT, DURATION);

            assertThat(segment.startNanoOfDay()).isEqualTo(START_AT.toNanoOfDay());
            assertThat(segment.endNanoOfDay()).isEqualTo(START_AT.toNanoOfDay() + DURATION.toNanos());
            assertThat(segment.duration()).isEqualTo(DURATION);
        }

        @Test
        @DisplayName("시작 시간과 지속 시간이 하루 경계를 넘으면 예외")
        void throw_exception_when_startAt_and_duration_exceeds_day_boundary() {
            assertThatThrownBy(() -> SimpleDailyTimeSegment.of(LocalTime.of(23, 0), Duration.ofHours(2)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
