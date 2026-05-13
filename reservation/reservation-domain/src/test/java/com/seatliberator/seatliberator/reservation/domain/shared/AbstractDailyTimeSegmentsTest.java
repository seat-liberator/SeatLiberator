package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegmentsTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class AbstractDailyTimeSegmentsTest<T extends DailyTimeSegments> {

    public abstract T create(List<DailyTimeSegment> segments);

    @Test
    @DisplayName("DailyTimeSegment 목록을 시작 나노 기준으로 정렬할 수 있다")
    void sort_segments_by_start_nano_of_day() {
        var segments = create(List.of(
                segment("18:00", "20:00"),
                segment("09:00", "12:00"),
                segment("13:00", "17:00")
        ));

        assertThat(segments.segments())
                .extracting(DailyTimeSegment::startNanoOfDay)
                .containsExactly(
                        nanoOf("09:00"),
                        nanoOf("13:00"),
                        nanoOf("18:00")
                );
    }

    @Test
    @DisplayName("DailyTimeSegment 목록이 비어있으면 예외")
    void throw_exception_when_segments_is_empty() {
        assertThatThrownBy(() -> create(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("segments must not be empty.");
    }

    @Test
    @DisplayName("DailyTimeSegment 목록에 null이 있으면 예외")
    void throw_exception_when_segment_is_null() {
        assertThatDomainThrownBy(() -> create(Arrays.asList(segment("09:00", "12:00"), null)))
                .hasNonNullMessageFor("segment");
    }

    @Test
    @DisplayName("DailyTimeSegment 목록에 겹치는 구간이 있으면 예외")
    void throw_exception_when_segments_overlap() {
        assertThatThrownBy(() -> create(List.of(
                segment("09:00", "12:00"),
                segment("11:00", "13:00")
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("segments must not overlap.");
    }

    @Test
    @DisplayName("DailyTimeSegment 목록에 인접한 구간이 있으면 허용한다")
    void allow_adjacent_segments() {
        var segments = create(List.of(
                segment("09:00", "12:00"),
                segment("12:00", "18:00")
        ));

        assertThat(segments.segments()).hasSize(2);
    }

    @Test
    @DisplayName("DailyTimeSegment 목록은 값으로 복사한다")
    void copy_segments_by_value() {
        var mutable = EmbeddableDailyTimeSegment.of(nanoOf("09:00"), nanoOf("12:00"));
        var segments = create(List.of(mutable));

        mutable.updateStartNanoOfDay(nanoOf("10:00"));

        assertThat(segments.segments().getFirst().startNanoOfDay()).isEqualTo(nanoOf("09:00"));
        assertThat(segments.segments().getFirst().endNanoOfDay()).isEqualTo(nanoOf("12:00"));
    }

    @Test
    @DisplayName("nanoOfDay 포함 여부를 확인할 수 있다")
    void contains_nano_of_day() {
        var segments = create(List.of(
                segment("09:00", "12:00"),
                segment("13:00", "18:00")
        ));

        assertThat(segments.contains(nanoOf("09:00"))).isTrue();
        assertThat(segments.contains(nanoOf("12:00"))).isFalse();
        assertThat(segments.contains(nanoOf("13:00"))).isTrue();
        assertThat(segments.contains(nanoOf("18:00"))).isFalse();
    }

    @Test
    @DisplayName("LocalTime 포함 여부를 확인할 수 있다")
    void contains_local_time() {
        var segments = create(List.of(segment("09:00", "18:00")));

        assertThat(segments.contains(LocalTime.of(9, 0))).isTrue();
        assertThat(segments.contains(LocalTime.of(18, 0))).isFalse();

        assertThatDomainThrownBy(() -> segments.contains((LocalTime) null))
                .hasNonNullMessageFor("other");
    }

    @Test
    @DisplayName("Instant 포함 여부를 ZoneId 기준으로 확인할 수 있다")
    void contains_instant_with_zone_id() {
        var segments = create(List.of(segment("09:00", "18:00")));

        assertThat(segments.contains(Instant.parse("2026-01-01T00:00:00Z"), SEOUL)).isTrue();
        assertThat(segments.contains(Instant.parse("2026-01-01T09:00:00Z"), SEOUL)).isFalse();

        assertThatDomainThrownBy(() -> segments.contains((Instant) null, SEOUL))
                .hasNonNullMessageFor("other");
        assertThatDomainThrownBy(() -> segments.contains(Instant.parse("2026-01-01T00:00:00Z"), null))
                .hasNonNullMessageFor("zoneId");
    }

    @Test
    @DisplayName("DailyTimeSegment가 하나의 구간에 포함되면 포함한다")
    void contains_daily_time_segment_with_single_segment() {
        var segments = create(List.of(segment("09:00", "18:00")));

        assertThat(segments.contains(segment("10:00", "17:00"))).isTrue();
        assertThat(segments.contains(segment("08:00", "17:00"))).isFalse();
        assertThat(segments.contains(segment("10:00", "19:00"))).isFalse();
    }

    @Test
    @DisplayName("DailyTimeSegment가 인접한 여러 구간으로 덮이면 포함한다")
    void contains_daily_time_segment_with_adjacent_segments() {
        var segments = create(List.of(
                segment("09:00", "12:00"),
                segment("12:00", "18:00")
        ));

        assertThat(segments.contains(segment("09:00", "18:00"))).isTrue();
    }

    @Test
    @DisplayName("DailyTimeSegment 사이에 빈 구간이 있으면 포함하지 않는다")
    void does_not_contain_daily_time_segment_when_gap_exists() {
        var segments = create(List.of(
                segment("09:00", "12:00"),
                segment("13:00", "18:00")
        ));

        assertThat(segments.contains(segment("09:00", "18:00"))).isFalse();
        assertThatDomainThrownBy(() -> segments.contains((DailyTimeSegment) null))
                .hasNonNullMessageFor("other");
    }

    @Test
    @DisplayName("하루 전체를 포함하면 always segments이다")
    void always_segments() {
        assertThat(create(List.of(segment("00:00", DailyTimeSegment.DAY_NANOS))).isAlways()).isTrue();
        assertThat(create(List.of(segment("00:00", "23:59"))).isAlways()).isFalse();
    }

    @Test
    @DisplayName("InstantRange가 하루 안에서 DailyTimeSegments에 포함되면 포함한다")
    void contains_instant_range_within_day() {
        var segments = create(List.of(segment("09:00", "18:00")));

        assertThat(segments.contains(withinDayInstantRange(), SEOUL)).isTrue();
    }

    @Test
    @DisplayName("InstantRange가 자정을 넘어도 날짜별 구간이 모두 포함되면 포함한다")
    void contains_instant_range_crossing_midnight() {
        var segments = create(List.of(
                segment("00:00", "02:00"),
                segment("22:00", DailyTimeSegment.DAY_NANOS)
        ));

        assertThat(segments.contains(crossingMidnightInstantRange(), SEOUL)).isTrue();
    }

    @Test
    @DisplayName("InstantRange가 자정을 넘을 때 날짜별 구간 중 하나라도 비어있으면 포함하지 않는다")
    void does_not_contain_instant_range_crossing_midnight_when_gap_exists() {
        var segments = create(List.of(segment("22:00", DailyTimeSegment.DAY_NANOS)));

        assertThat(segments.contains(crossingMidnightInstantRange(), SEOUL)).isFalse();
    }

    @Test
    @DisplayName("InstantRange가 24시간 이상이어도 하루 전체 스케줄이면 포함한다")
    void contains_instant_range_longer_than_day_when_always() {
        var segments = create(List.of(segment("00:00", DailyTimeSegment.DAY_NANOS)));

        assertThat(segments.contains(longerThanDayInstantRange(), UTC)).isTrue();
    }

    @Test
    @DisplayName("InstantRange가 24시간 이상이면 각 날짜별 구간을 모두 검사한다")
    void contains_instant_range_longer_than_day_checks_every_daily_chunk() {
        var segments = create(List.of(
                segment("00:00", "12:00"),
                segment("13:00", DailyTimeSegment.DAY_NANOS)
        ));

        assertThat(segments.contains(longChunkCheckedInstantRange(), UTC)).isFalse();
    }

    @Test
    @DisplayName("InstantRange가 정확히 자정에 끝나면 종료 경계를 하루 끝으로 취급한다")
    void contains_instant_range_ending_at_midnight() {
        var segments = create(List.of(segment("22:00", DailyTimeSegment.DAY_NANOS)));

        assertThat(segments.contains(endingAtMidnightInstantRange(), SEOUL)).isTrue();
    }

    @Test
    @DisplayName("InstantRange가 여러 날짜에 걸치면 모든 날짜별 조각을 검사한다")
    void contains_instant_range_checks_all_daily_chunks() {
        var segments = create(List.of(
                segment("00:00", "02:00"),
                segment("22:00", DailyTimeSegment.DAY_NANOS)
        ));

        assertThat(segments.contains(multiDayChunkInstantRange(), UTC)).isFalse();
    }

    @Test
    @DisplayName("InstantRange 포함 여부를 DST 전환일에도 local date boundary 기준으로 확인한다")
    void contains_instant_range_on_dst_transition_day() {
        var segments = create(List.of(segment("00:00", DailyTimeSegment.DAY_NANOS)));

        assertThat(segments.contains(dstTransitionInstantRange(), NEW_YORK)).isTrue();
    }

    @Test
    @DisplayName("InstantRange 포함 여부 검증 대상이 null이면 예외")
    void throw_exception_when_instant_range_or_zone_id_is_null() {
        var segments = create(List.of(segment("00:00", DailyTimeSegment.DAY_NANOS)));
        var range = SimpleInstantRange.of(
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T01:00:00Z")
        );

        assertThatDomainThrownBy(() -> segments.contains((InstantRange) null, UTC))
                .hasNonNullMessageFor("other");
        assertThatDomainThrownBy(() -> segments.contains(range, null))
                .hasNonNullMessageFor("zoneId");
    }

    @Test
    @DisplayName("하나 이상의 구간이 대상 구간과 겹치면 true")
    void return_true_when_any_segment_overlaps_target_segment() {
        var segments = create(List.of(
                segment("09:00", "12:00"),
                segment("13:00", "18:00")
        ));

        assertThat(segments.overlaps(segment("12:00", DailyTimeSegment.DAY_NANOS))).isTrue();
    }

    @Test
    @DisplayName("어떤 구간도 대상 구간과 겹치지 않으면 false")
    void return_false_when_no_segment_overlaps_target_segment() {
        var segments = create(List.of(
                segment("09:00", "12:00"),
                segment("13:00", "18:00")
        ));

        assertThat(segments.overlaps(segment("12:00", "13:00"))).isFalse();
    }

    @Test
    @DisplayName("하나 이상의 구간이 다른 DailyTimeSegments와 겹치면 true")
    void return_true_when_any_segment_overlaps_other_segments() {
        var segments = create(List.of(
                segment("09:00", "12:00"),
                segment("13:00", "18:00")
        ));

        var other = create(List.of(
                segment("12:00", DailyTimeSegment.DAY_NANOS)
        ));

        assertThat(segments.overlaps(other)).isTrue();
    }

    @Test
    @DisplayName("어떤 구간도 다른 DailyTimeSegments와 겹치지 않으면 false")
    void return_false_when_no_segment_overlaps_other_segments() {
        var segments = create(List.of(
                segment("09:00", "12:00"),
                segment("13:00", "18:00")
        ));

        var other = create(List.of(
                segment("12:00", "13:00")
        ));

        assertThat(segments.overlaps(other)).isFalse();
    }

    @Test
    @DisplayName("겹침 여부 조회 인자가 null이면 예외")
    void throw_exception_when_overlaps_argument_is_null() {
        var segments = create(List.of(
                segment("09:00", "12:00"),
                segment("13:00", "18:00")
        ));

        assertThatThrownBy(() -> segments.overlaps((DailyTimeSegment) null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> segments.overlaps((DailyTimeSegments) null))
                .isInstanceOf(NullPointerException.class);
    }

}
