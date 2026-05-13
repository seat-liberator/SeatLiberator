package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegmentTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SimpleDailyTimeSegment 도메인 테스트")
public class SimpleDailyTimeSegmentTest extends AbstractDailyTimeSegmentTest<SimpleDailyTimeSegment> {

    @Override
    public SimpleDailyTimeSegment create(long startNanoOfDay, long endNanoOfDay) {
        return SimpleDailyTimeSegment.of(startNanoOfDay, endNanoOfDay);
    }

    @Test
    @DisplayName("시작 나노와 Duration으로 생성할 수 있다")
    void create_with_start_nano_of_day_and_duration() {
        var range = SimpleDailyTimeSegment.of(START_NANO_OF_DAY, DURATION);

        assertThat(range.startNanoOfDay()).isEqualTo(START_NANO_OF_DAY);
        assertThat(range.endNanoOfDay()).isEqualTo(END_NANO_OF_DAY);
    }

    @Test
    @DisplayName("시작 시간과 Duration으로 생성할 수 있다")
    void create_with_start_at_and_duration() {
        var range = SimpleDailyTimeSegment.of(LocalTime.ofNanoOfDay(START_NANO_OF_DAY), DURATION);

        assertThat(range.startNanoOfDay()).isEqualTo(START_NANO_OF_DAY);
        assertThat(range.endNanoOfDay()).isEqualTo(END_NANO_OF_DAY);
    }

    @Test
    @DisplayName("Duration이 양수가 아니면 예외")
    void throw_exception_when_duration_is_not_positive() {
        assertThatDomainThrownBy(() -> SimpleDailyTimeSegment.of(START_NANO_OF_DAY, Duration.ZERO))
                .hasPositiveMessageFor("duration");

        assertThatDomainThrownBy(() -> SimpleDailyTimeSegment.of(START_NANO_OF_DAY, Duration.ofNanos(-1)))
                .hasPositiveMessageFor("duration");
    }

    @Test
    @DisplayName("DailyNanoRange로부터 복사할 수 있다")
    void copy_from_daily_nano_range() {
        var range = SimpleDailyTimeSegment.from(create(START_NANO_OF_DAY, END_NANO_OF_DAY));

        assertThat(range.startNanoOfDay()).isEqualTo(START_NANO_OF_DAY);
        assertThat(range.endNanoOfDay()).isEqualTo(END_NANO_OF_DAY);

        assertThatDomainThrownBy(() -> SimpleDailyTimeSegment.from(null))
                .hasNonNullMessageFor("segment");
    }
}
