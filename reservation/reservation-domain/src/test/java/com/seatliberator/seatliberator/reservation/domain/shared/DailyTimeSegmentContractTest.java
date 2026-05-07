package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public interface DailyTimeSegmentContractTest<T extends DailyTimeSegment> {

    T create(long startNanoOfDay, long endNanoOfDay);

    default T create(LocalTime startAt, Duration duration) {
        return create(startAt.toNanoOfDay(), startAt.toNanoOfDay() + duration.toNanos());
    }

    @Test
    @DisplayName("시작 나노와 종료 나노로 생성한다")
    default void create_with_startNanoOfDay_and_endNanoOfDay() {
        var segment = create(LocalTime.of(9, 0).toNanoOfDay(), LocalTime.of(12, 0).toNanoOfDay());

        assertThat(segment.startNanoOfDay()).isEqualTo(LocalTime.of(9, 0).toNanoOfDay());
        assertThat(segment.endNanoOfDay()).isEqualTo(LocalTime.of(12, 0).toNanoOfDay());
        assertThat(segment.startAt()).isEqualTo(LocalTime.of(9, 0));
        assertThat(segment.duration()).isEqualTo(Duration.ofHours(3));
    }

    @Test
    @DisplayName("하루 마지막 경계까지 포함하는 구간을 생성한다")
    default void create_segment_until_end_of_day_boundary() {
        var segment = create(LocalTime.of(18, 0).toNanoOfDay(), DailyTimeSegment.DAY_NANOS);

        assertThat(segment.endNanoOfDay()).isEqualTo(DailyTimeSegment.DAY_NANOS);
        assertThat(segment.duration()).isEqualTo(Duration.ofHours(6));
    }

    @Test
    @DisplayName("시작 나노가 음수이면 예외")
    default void throw_exception_when_startNanoOfDay_is_negative() {
        assertThatThrownBy(() -> create(-1, LocalTime.of(12, 0).toNanoOfDay()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("종료 나노가 하루 경계를 넘으면 예외")
    default void throw_exception_when_endNanoOfDay_exceeds_day_boundary() {
        assertThatThrownBy(() -> create(LocalTime.of(9, 0).toNanoOfDay(), DailyTimeSegment.DAY_NANOS + 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("시작 나노가 종료 나노와 같거나 이후이면 예외")
    default void throw_exception_when_startNanoOfDay_is_not_before_endNanoOfDay() {
        var nanoOfDay = LocalTime.of(9, 0).toNanoOfDay();

        assertThatThrownBy(() -> create(nanoOfDay, nanoOfDay))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> create(nanoOfDay, nanoOfDay - 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간은 시작 경계를 포함하고 종료 경계를 포함하지 않는다")
    default void contains_start_boundary_but_not_end_boundary() {
        var segment = create(LocalTime.of(9, 0), Duration.ofHours(3));

        assertThat(segment.contains(LocalTime.of(9, 0))).isTrue();
        assertThat(segment.contains(LocalTime.of(11, 59, 59, 999_999_999))).isTrue();
        assertThat(segment.contains(LocalTime.of(12, 0))).isFalse();
    }

    @Test
    @DisplayName("하루 범위를 벗어난 시각 포함 여부 조회는 예외")
    default void throw_exception_when_contains_nanoOfDay_out_of_day_range() {
        var segment = create(LocalTime.of(9, 0), Duration.ofHours(3));

        assertThatThrownBy(() -> segment.contains(-1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> segment.contains(DailyTimeSegment.DAY_NANOS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("다른 구간을 완전히 포함하면 true")
    default void return_true_when_contains_other_segment() {
        var segment = create(LocalTime.of(9, 0), Duration.ofHours(8));
        var other = create(LocalTime.of(10, 0), Duration.ofHours(2));

        assertThat(segment.contains(other)).isTrue();
    }

    @Test
    @DisplayName("다른 구간을 완전히 포함하지 않으면 false")
    default void return_false_when_does_not_contain_other_segment() {
        var segment = create(LocalTime.of(9, 0), Duration.ofHours(3));
        var other = create(LocalTime.of(11, 0), Duration.ofHours(2));

        assertThat(segment.contains(other)).isFalse();
    }

    @Test
    @DisplayName("두 구간이 겹치면 true")
    default void return_true_when_segments_overlap() {
        var left = create(LocalTime.of(9, 0), Duration.ofHours(3));
        var right = create(LocalTime.of(11, 0), Duration.ofHours(3));

        assertThat(left.overlaps(right)).isTrue();
        assertThat(right.overlaps(left)).isTrue();
    }

    @Test
    @DisplayName("두 구간이 겹치지 않으면 false")
    default void return_false_when_segments_do_not_overlap() {
        var left = create(LocalTime.of(9, 0), Duration.ofHours(3));
        var right = create(LocalTime.of(13, 0), Duration.ofHours(3));

        assertThat(left.overlaps(right)).isFalse();
        assertThat(right.overlaps(left)).isFalse();
    }

    @Test
    @DisplayName("한 구간의 종료와 다른 구간의 시작이 같으면 겹치지 않는다")
    default void return_false_when_segments_touch_at_boundary() {
        var left = create(LocalTime.of(9, 0), Duration.ofHours(3));
        var right = create(LocalTime.of(12, 0), Duration.ofHours(3));

        assertThat(left.overlaps(right)).isFalse();
        assertThat(right.overlaps(left)).isFalse();
    }

    @Test
    @DisplayName("시작과 종료가 모두 같으면 같은 구간이다")
    default void return_true_when_segments_are_same() {
        var segment = create(LocalTime.of(9, 0), Duration.ofHours(3));
        var other = create(LocalTime.of(9, 0), Duration.ofHours(3));

        assertThat(segment.isSame(other)).isTrue();
    }

    @Test
    @DisplayName("시작 또는 종료가 다르면 다른 구간이다")
    default void return_false_when_segment_boundary_is_different() {
        var segment = create(LocalTime.of(9, 0), Duration.ofHours(3));
        var other = create(LocalTime.of(10, 0), Duration.ofHours(2));

        assertThat(segment.isSame(other)).isFalse();
    }

    @Test
    @DisplayName("비교 대상 구간이 null이면 예외")
    default void throw_exception_when_other_segment_is_null() {
        var segment = create(LocalTime.of(9, 0), Duration.ofHours(3));

        assertThatThrownBy(() -> segment.contains((DailyTimeSegment) null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> segment.overlaps(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> segment.isSame(null))
                .isInstanceOf(NullPointerException.class);
    }
}
