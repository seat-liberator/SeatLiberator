package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyScheduleTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class AbstractDailyScheduleTest<T extends DailySchedule> {

    public abstract T create(List<DailyNanoRange> ranges);

    @Test
    @DisplayName("DailyNanoRange 목록을 시작 나노 기준으로 정렬할 수 있다")
    void sort_ranges_by_start_nano_of_day() {
        var ranges = create(List.of(
                range("18:00", "20:00"),
                range("09:00", "12:00"),
                range("13:00", "17:00")
        ));

        assertThat(ranges.ranges())
                .extracting(DailyNanoRange::startNanoOfDay)
                .containsExactly(
                        nanoOf("09:00"),
                        nanoOf("13:00"),
                        nanoOf("18:00")
                );
    }

    @Test
    @DisplayName("DailyNanoRange 목록이 비어있으면 예외")
    void throw_exception_when_ranges_is_empty() {
        assertThatThrownBy(() -> create(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ranges must not be empty.");
    }

    @Test
    @DisplayName("DailyNanoRange 목록에 null이 있으면 예외")
    void throw_exception_when_range_is_null() {
        assertThatDomainThrownBy(() -> create(Arrays.asList(range("09:00", "12:00"), null)))
                .hasNonNullMessageFor("range");
    }

    @Test
    @DisplayName("DailyNanoRange 목록에 겹치는 구간이 있으면 예외")
    void throw_exception_when_ranges_overlap() {
        assertThatThrownBy(() -> create(List.of(
                range("09:00", "12:00"),
                range("11:00", "13:00")
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ranges must not overlap.");
    }

    @Test
    @DisplayName("DailyNanoRange 목록에 인접한 구간이 있으면 허용한다")
    void allow_adjacent_ranges() {
        var ranges = create(List.of(
                range("09:00", "12:00"),
                range("12:00", "18:00")
        ));

        assertThat(ranges.ranges()).hasSize(2);
    }

    @Test
    @DisplayName("비어있는 DailyNanoRange 목록은 연속적이다")
    void empty_ranges_are_continuous() {
        assertThat(DailySchedule.isContinuous(List.of())).isTrue();
    }

    @Test
    @DisplayName("단일 DailyNanoRange 목록은 연속적이다")
    void single_range_is_continuous() {
        assertThat(DailySchedule.isContinuous(List.of(range("09:00", "12:00")))).isTrue();
    }

    @Test
    @DisplayName("DailyNanoRange 목록의 모든 구간이 맞닿아 있으면 연속적이다")
    void adjacent_ranges_are_continuous() {
        assertThat(DailySchedule.isContinuous(List.of(
                range("09:00", "12:00"),
                range("12:00", "15:00"),
                range("15:00", "18:00")
        ))).isTrue();
    }

    @Test
    @DisplayName("DailyNanoRange 목록은 정렬되지 않아도 연속 여부를 확인할 수 있다")
    void unsorted_adjacent_ranges_are_continuous() {
        assertThat(DailySchedule.isContinuous(List.of(
                range("15:00", "18:00"),
                range("09:00", "12:00"),
                range("12:00", "15:00")
        ))).isTrue();
    }

    @Test
    @DisplayName("DailyNanoRange 목록 사이에 빈 구간이 있으면 연속적이지 않다")
    void ranges_with_gap_are_not_continuous() {
        assertThat(DailySchedule.isContinuous(List.of(
                range("09:00", "12:00"),
                range("13:00", "18:00")
        ))).isFalse();
    }

    @Test
    @DisplayName("DailyNanoRange 목록에 겹치는 구간이 있으면 연속적이지 않다")
    void overlapping_ranges_are_not_continuous() {
        assertThat(DailySchedule.isContinuous(List.of(
                range("09:00", "13:00"),
                range("12:00", "18:00")
        ))).isFalse();
    }

    @Test
    @DisplayName("DailyNanoRange 목록 연속 여부 조회 인자가 null이면 예외")
    void throw_exception_when_is_continuous_argument_is_null() {
        assertThatDomainThrownBy(() -> DailySchedule.isContinuous(null))
                .hasNonNullMessageFor("ranges");
    }

    @Test
    @DisplayName("DailyNanoRange 목록은 값으로 복사한다")
    void copy_ranges_by_value() {
        var mutable = EmbeddableDailyNanoRange.of(nanoOf("09:00"), nanoOf("12:00"));
        var ranges = create(List.of(mutable));

        mutable.updateStartNanoOfDay(nanoOf("10:00"));

        assertThat(ranges.ranges().getFirst().startNanoOfDay()).isEqualTo(nanoOf("09:00"));
        assertThat(ranges.ranges().getFirst().endNanoOfDay()).isEqualTo(nanoOf("12:00"));
    }

    @Test
    @DisplayName("nanoOfDay 포함 여부를 확인할 수 있다")
    void contains_nano_of_day() {
        var ranges = create(List.of(
                range("09:00", "12:00"),
                range("13:00", "18:00")
        ));

        assertThat(ranges.contains(nanoOf("09:00"))).isTrue();
        assertThat(ranges.contains(nanoOf("12:00"))).isFalse();
        assertThat(ranges.contains(nanoOf("13:00"))).isTrue();
        assertThat(ranges.contains(nanoOf("18:00"))).isFalse();
    }

    @Test
    @DisplayName("LocalTime 포함 여부를 확인할 수 있다")
    void contains_local_time() {
        var ranges = create(List.of(range("09:00", "18:00")));

        assertThat(ranges.contains(LocalTime.of(9, 0))).isTrue();
        assertThat(ranges.contains(LocalTime.of(18, 0))).isFalse();

        assertThatDomainThrownBy(() -> ranges.contains((LocalTime) null))
                .hasNonNullMessageFor("other");
    }

    @Test
    @DisplayName("Instant 포함 여부를 ZoneId 기준으로 확인할 수 있다")
    void contains_instant_with_zone_id() {
        var ranges = create(List.of(range("09:00", "18:00")));

        assertThat(ranges.contains(Instant.parse("2026-01-01T00:00:00Z"), SEOUL)).isTrue();
        assertThat(ranges.contains(Instant.parse("2026-01-01T09:00:00Z"), SEOUL)).isFalse();

        assertThatDomainThrownBy(() -> ranges.contains((Instant) null, SEOUL))
                .hasNonNullMessageFor("other");
        assertThatDomainThrownBy(() -> ranges.contains(Instant.parse("2026-01-01T00:00:00Z"), null))
                .hasNonNullMessageFor("zoneId");
    }

    @Test
    @DisplayName("DailyNanoRange가 하나의 구간에 포함되면 포함한다")
    void contains_daily_time_range_with_single_range() {
        var ranges = create(List.of(range("09:00", "18:00")));

        assertThat(ranges.contains(range("10:00", "17:00"))).isTrue();
        assertThat(ranges.contains(range("08:00", "17:00"))).isFalse();
        assertThat(ranges.contains(range("10:00", "19:00"))).isFalse();
    }

    @Test
    @DisplayName("DailyNanoRange가 인접한 여러 구간으로 덮이면 포함한다")
    void contains_daily_time_range_with_adjacent_ranges() {
        var ranges = create(List.of(
                range("09:00", "12:00"),
                range("12:00", "18:00")
        ));

        assertThat(ranges.contains(range("09:00", "18:00"))).isTrue();
    }

    @Test
    @DisplayName("DailyNanoRange 사이에 빈 구간이 있으면 포함하지 않는다")
    void does_not_contain_daily_time_range_when_gap_exists() {
        var ranges = create(List.of(
                range("09:00", "12:00"),
                range("13:00", "18:00")
        ));

        assertThat(ranges.contains(range("09:00", "18:00"))).isFalse();
        assertThatDomainThrownBy(() -> ranges.contains((DailyNanoRange) null))
                .hasNonNullMessageFor("other");
    }

    @Test
    @DisplayName("하루 전체를 포함하면 always ranges이다")
    void always_ranges() {
        assertThat(create(List.of(range("00:00", DailyNanoRange.DAY_NANOS))).isAlways()).isTrue();
        assertThat(create(List.of(range("00:00", "23:59"))).isAlways()).isFalse();
    }

    @Test
    @DisplayName("InstantRange가 하루 안에서 DailyNanoRanges에 포함되면 포함한다")
    void contains_instant_range_within_day() {
        var ranges = create(List.of(range("09:00", "18:00")));

        assertThat(ranges.contains(withinDayInstantRange(), SEOUL)).isTrue();
    }

    @Test
    @DisplayName("InstantRange가 자정을 넘어도 날짜별 구간이 모두 포함되면 포함한다")
    void contains_instant_range_crossing_midnight() {
        var ranges = create(List.of(
                range("00:00", "02:00"),
                range("22:00", DailyNanoRange.DAY_NANOS)
        ));

        assertThat(ranges.contains(crossingMidnightInstantRange(), SEOUL)).isTrue();
    }

    @Test
    @DisplayName("InstantRange가 자정을 넘을 때 날짜별 구간 중 하나라도 비어있으면 포함하지 않는다")
    void does_not_contain_instant_range_crossing_midnight_when_gap_exists() {
        var ranges = create(List.of(range("22:00", DailyNanoRange.DAY_NANOS)));

        assertThat(ranges.contains(crossingMidnightInstantRange(), SEOUL)).isFalse();
    }

    @Test
    @DisplayName("InstantRange가 24시간 이상이어도 하루 전체 스케줄이면 포함한다")
    void contains_instant_range_longer_than_day_when_always() {
        var ranges = create(List.of(range("00:00", DailyNanoRange.DAY_NANOS)));

        assertThat(ranges.contains(longerThanDayInstantRange(), UTC)).isTrue();
    }

    @Test
    @DisplayName("InstantRange가 24시간 이상이면 각 날짜별 구간을 모두 검사한다")
    void contains_instant_range_longer_than_day_checks_every_daily_chunk() {
        var ranges = create(List.of(
                range("00:00", "12:00"),
                range("13:00", DailyNanoRange.DAY_NANOS)
        ));

        assertThat(ranges.contains(longChunkCheckedInstantRange(), UTC)).isFalse();
    }

    @Test
    @DisplayName("InstantRange가 정확히 자정에 끝나면 종료 경계를 하루 끝으로 취급한다")
    void contains_instant_range_ending_at_midnight() {
        var ranges = create(List.of(range("22:00", DailyNanoRange.DAY_NANOS)));

        assertThat(ranges.contains(endingAtMidnightInstantRange(), SEOUL)).isTrue();
    }

    @Test
    @DisplayName("InstantRange가 여러 날짜에 걸치면 모든 날짜별 조각을 검사한다")
    void contains_instant_range_checks_all_daily_chunks() {
        var ranges = create(List.of(
                range("00:00", "02:00"),
                range("22:00", DailyNanoRange.DAY_NANOS)
        ));

        assertThat(ranges.contains(multiDayChunkInstantRange(), UTC)).isFalse();
    }

    @Test
    @DisplayName("InstantRange 포함 여부를 DST 전환일에도 local date boundary 기준으로 확인한다")
    void contains_instant_range_on_dst_transition_day() {
        var ranges = create(List.of(range("00:00", DailyNanoRange.DAY_NANOS)));

        assertThat(ranges.contains(dstTransitionInstantRange(), NEW_YORK)).isTrue();
    }

    @Test
    @DisplayName("InstantRange 포함 여부 검증 대상이 null이면 예외")
    void throw_exception_when_instant_range_or_zone_id_is_null() {
        var ranges = create(List.of(range("00:00", DailyNanoRange.DAY_NANOS)));
        var range = SimpleInstantRange.of(
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T01:00:00Z")
        );

        assertThatDomainThrownBy(() -> ranges.contains((InstantRange) null, UTC))
                .hasNonNullMessageFor("other");
        assertThatDomainThrownBy(() -> ranges.contains(range, null))
                .hasNonNullMessageFor("zoneId");
    }

    @Test
    @DisplayName("하나 이상의 구간이 대상 구간과 겹치면 true")
    void return_true_when_any_range_overlaps_target_range() {
        var ranges = create(List.of(
                range("09:00", "12:00"),
                range("13:00", "18:00")
        ));

        assertThat(ranges.overlaps(range("12:00", DailyNanoRange.DAY_NANOS))).isTrue();
    }

    @Test
    @DisplayName("어떤 구간도 대상 구간과 겹치지 않으면 false")
    void return_false_when_no_range_overlaps_target_range() {
        var ranges = create(List.of(
                range("09:00", "12:00"),
                range("13:00", "18:00")
        ));

        assertThat(ranges.overlaps(range("12:00", "13:00"))).isFalse();
    }

    @Test
    @DisplayName("하나 이상의 구간이 다른 DailyNanoRanges와 겹치면 true")
    void return_true_when_any_range_overlaps_other_ranges() {
        var ranges = create(List.of(
                range("09:00", "12:00"),
                range("13:00", "18:00")
        ));

        var other = create(List.of(
                range("12:00", DailyNanoRange.DAY_NANOS)
        ));

        assertThat(ranges.overlaps(other)).isTrue();
    }

    @Test
    @DisplayName("어떤 구간도 다른 DailyNanoRanges와 겹치지 않으면 false")
    void return_false_when_no_range_overlaps_other_ranges() {
        var ranges = create(List.of(
                range("09:00", "12:00"),
                range("13:00", "18:00")
        ));

        var other = create(List.of(
                range("12:00", "13:00")
        ));

        assertThat(ranges.overlaps(other)).isFalse();
    }

    @Test
    @DisplayName("겹침 여부 조회 인자가 null이면 예외")
    void throw_exception_when_overlaps_argument_is_null() {
        var ranges = create(List.of(
                range("09:00", "12:00"),
                range("13:00", "18:00")
        ));

        assertThatThrownBy(() -> ranges.overlaps((DailyNanoRange) null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> ranges.overlaps((DailySchedule) null))
                .isInstanceOf(NullPointerException.class);
    }

}
