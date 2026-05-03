package com.seatliberator.seatliberator.reservation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public interface TimeRangeContractTest<T extends TimeRange> {

    T create(Instant startAt, Instant endAt);

    Instant getStartAt();

    Instant getEndAt();

    @Test
    @DisplayName("시작 시간과 종료 시간을 가진 TimeRange를 만들 수 있다")
    default void create_range_when_arguments_are_valid() {
        var startAt = getStartAt();
        var endAt = getEndAt();

        var range = create(startAt, endAt);

        assertThat(range.startAt()).isEqualTo(startAt);
        assertThat(range.endAt()).isEqualTo(endAt);
    }

    @Test
    @DisplayName("시작 시간 이상 종료 시간 미만이면 포함한다")
    default void contains_time_within_range() {
        var startAt = getStartAt();
        var endAt = getEndAt();
        var range = create(startAt, endAt);

        assertThat(range.contains(startAt)).isTrue();
        assertThat(range.contains(endAt.minusNanos(1))).isTrue();
    }

    @Test
    @DisplayName("시작 시간 이전과 종료 시간 이후는 포함하지 않는다")
    default void does_not_contain_time_outside_range() {
        var startAt = getStartAt();
        var endAt = getEndAt();
        var range = create(startAt, endAt);

        assertThat(range.contains(startAt.minusNanos(1))).isFalse();
        assertThat(range.contains(endAt)).isFalse();
    }

    @Test
    @DisplayName("종료 시간 이상이면 종료된 상태다")
    default void return_true_when_time_is_on_or_after_end_at() {
        var startAt = getStartAt();
        var endAt = getEndAt();
        var range = create(startAt, endAt);

        assertThat(range.isEnded(endAt.minusNanos(1))).isFalse();
        assertThat(range.isEnded(endAt)).isTrue();
        assertThat(range.isEnded(endAt.plusSeconds(1))).isTrue();
    }

    @Test
    @DisplayName("시작 시간과 종료 시간이 같으면 isSame은 true다")
    default void is_same_with_same_value() {
        var startAt = getStartAt();
        var endAt = getEndAt();
        var range = create(startAt, endAt);
        var other = create(startAt, endAt);

        assertThat(range.isSame(other)).isTrue();
    }

    @Test
    @DisplayName("시작 시간 또는 종료 시간이 다르면 isSame은 false다")
    default void is_not_same_when_value_is_different() {
        var startAt = getStartAt();
        var endAt = getEndAt();
        var range = create(startAt, endAt);
        var differentStart = create(startAt.plusSeconds(1), endAt.plusSeconds(1));
        var differentEnd = create(startAt, endAt.plusSeconds(1));

        assertThat(range.isSame(differentStart)).isFalse();
        assertThat(range.isSame(differentEnd)).isFalse();
    }

    @Test
    @DisplayName("서로 다른 TimeRange 구현체라도 같은 시간이면 isSame은 true다")
    default void is_same_across_different_implementations() {
        var startAt = getStartAt();
        var endAt = getEndAt();
        var simple = SimpleTimeRange.of(startAt, endAt);
        var embeddable = EmbeddableTimeRange.from(startAt, endAt);

        assertThat(simple.isSame(embeddable)).isTrue();
        assertThat(embeddable.isSame(simple)).isTrue();
    }
}
