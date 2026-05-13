package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegmentTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractDailyTimeSegmentTest<T extends DailyTimeSegment> extends AbstractRangeComparableTest<DailyTimeSegment> {

    public abstract T create(long startNanoOfDay, long endNanoOfDay);

    @BeforeEach
    void run() {
        range = create(START_NANO_OF_DAY, END_NANO_OF_DAY);
    }

    @Test
    @DisplayName("시작 나노와 종료 나노로 하루 내 시간 구간을 생성할 수 있다")
    void create_with_start_and_end_nano_of_day() {
        assertThat(range.startNanoOfDay()).isEqualTo(START_NANO_OF_DAY);
        assertThat(range.endNanoOfDay()).isEqualTo(END_NANO_OF_DAY);
        assertThat(range.startAt()).isEqualTo(START_AT);
        assertThat(range.duration()).isEqualTo(DURATION);
    }

    @Test
    @DisplayName("하루 전체 시간 구간을 생성할 수 있다")
    void create_whole_day_range() {
        var wholeDay = create(0L, DailyTimeSegment.DAY_NANOS);

        assertThat(wholeDay.startNanoOfDay()).isZero();
        assertThat(wholeDay.endNanoOfDay()).isEqualTo(DailyTimeSegment.DAY_NANOS);
        assertThat(wholeDay.duration()).isEqualTo(Duration.ofDays(1));
    }

    @Test
    @DisplayName("시작 나노가 종료 나노보다 같거나 이후면 예외")
    void throw_exception_when_start_nano_of_day_is_after_end_nano_of_day() {
        assertThatThrownBy(() -> create(START_NANO_OF_DAY, START_NANO_OF_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startNanoOfDay must be before endNanoOfDay.");

        assertThatThrownBy(() -> create(END_NANO_OF_DAY, START_NANO_OF_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startNanoOfDay must be before endNanoOfDay.");
    }

    @Test
    @DisplayName("시작 나노가 하루 범위를 벗어나면 예외")
    void throw_exception_when_start_nano_of_day_is_out_of_day() {
        assertThatThrownBy(() -> create(-1L, END_NANO_OF_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startNanoOfDay must be between 0 and 86399999999999.");

        assertThatThrownBy(() -> create(DailyTimeSegment.DAY_NANOS, DailyTimeSegment.DAY_NANOS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startNanoOfDay must be between 0 and 86399999999999.");
    }

    @Test
    @DisplayName("종료 나노가 하루 범위를 벗어나면 예외")
    void throw_exception_when_end_nano_of_day_is_out_of_day() {
        assertThatThrownBy(() -> create(START_NANO_OF_DAY, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("endNanoOfDay must be between 1 and 86400000000000.");

        assertThatThrownBy(() -> create(START_NANO_OF_DAY, DailyTimeSegment.DAY_NANOS + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("endNanoOfDay must be between 1 and 86400000000000.");
    }

    @Test
    @DisplayName("nanoOfDay 포함 여부는 시작 경계는 포함하고, 종료 경계는 포함하지 않는다")
    void contains_nano_of_day_start_boundary_but_not_end_boundary() {
        assertThat(range.contains(START_NANO_OF_DAY)).isTrue();
        assertThat(range.contains(BEFORE_END_NANO_OF_DAY)).isTrue();

        assertThat(range.contains(BEFORE_START_NANO_OF_DAY)).isFalse();
        assertThat(range.contains(END_NANO_OF_DAY)).isFalse();
    }

    @Test
    @DisplayName("nanoOfDay가 하루 범위를 벗어나면 예외")
    void throw_exception_when_nano_of_day_is_out_of_day() {
        assertThatThrownBy(() -> range.contains(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("nanoOfDay must be between 0 and 86399999999999.");

        assertThatThrownBy(() -> range.contains(DailyTimeSegment.DAY_NANOS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("nanoOfDay must be between 0 and 86399999999999.");
    }

    @Test
    @DisplayName("LocalTime 포함 여부를 확인할 수 있다")
    void contains_local_time() {
        assertThat(range.contains(START_AT)).isTrue();
        assertThat(range.contains(LocalTime.of(21, 0))).isTrue();
        assertThat(range.contains(LocalTime.of(22, 0))).isFalse();

        assertThatThrownBy(() -> range.contains((LocalTime) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("other must not be null.");
    }

    @Test
    @DisplayName("Instant 포함 여부를 ZoneId 기준으로 확인할 수 있다")
    void contains_instant_with_zone_id() {
        var zoneId = ZoneId.of("Asia/Seoul");
        var startAt = Instant.parse("2026-01-01T03:00:00Z");
        var beforeStartAt = Instant.parse("2026-01-01T02:59:59Z");
        var endAt = Instant.parse("2026-01-01T13:00:00Z");

        assertThat(range.contains(startAt, zoneId)).isTrue();
        assertThat(range.contains(beforeStartAt, zoneId)).isFalse();
        assertThat(range.contains(endAt, zoneId)).isFalse();

        assertThatThrownBy(() -> range.contains((Instant) null, zoneId))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("other must not be null.");
        assertThatThrownBy(() -> range.contains(startAt, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("zoneId must not be null.");
    }

    @Override
    protected T createSameRange() {
        return create(START_NANO_OF_DAY, END_NANO_OF_DAY);
    }

    @Override
    protected T createContainedRange() {
        return create(AFTER_START_NANO_OF_DAY, BEFORE_END_NANO_OF_DAY);
    }

    @Override
    protected T createContainingRange() {
        return create(BEFORE_START_NANO_OF_DAY, AFTER_END_NANO_OF_DAY);
    }

    @Override
    protected T createBeforeRange() {
        return create(BEFORE_START_NANO_OF_DAY, START_NANO_OF_DAY);
    }

    @Override
    protected T createAfterRange() {
        return create(END_NANO_OF_DAY, AFTER_END_NANO_OF_DAY);
    }

    @Override
    protected T createOverlapsBeforeRange() {
        return create(BEFORE_START_NANO_OF_DAY, AFTER_START_NANO_OF_DAY);
    }

    @Override
    protected T createOverlapsAfterRange() {
        return create(BEFORE_END_NANO_OF_DAY, AFTER_END_NANO_OF_DAY);
    }
}
