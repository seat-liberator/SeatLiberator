package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SimpleDailyTimeSegments 도메인 테스트")
class SimpleDailyTimeSegmentsTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("여러 DailyTimeSegment를 시작 시각 기준으로 정렬해 생성한다")
        void create_segments_sorted_by_start_time() {
            var segments = SimpleDailyTimeSegments.of(List.of(
                    segment(LocalTime.of(13, 0), Duration.ofHours(2)),
                    segment(LocalTime.of(1, 0), Duration.ofHours(2)),
                    segment(LocalTime.of(8, 0), Duration.ofHours(2))
            ));

            assertThat(segments.segments())
                    .extracting(DailyTimeSegment::startAt)
                    .containsExactly(
                            LocalTime.of(1, 0),
                            LocalTime.of(8, 0),
                            LocalTime.of(13, 0)
                    );
        }

        @Test
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_segments_is_null() {
            assertThatThrownBy(() -> SimpleDailyTimeSegments.of(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("구간 목록이 비어 있으면 예외")
        void throw_exception_when_segments_is_empty() {
            assertThatThrownBy(() -> SimpleDailyTimeSegments.of(List.of()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("구간 목록에 null이 있으면 예외")
        void throw_exception_when_segment_element_is_null() {
            assertThatThrownBy(() -> new SimpleDailyTimeSegments(listWithNullSegment()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("구간끼리 겹치면 예외")
        void throw_exception_when_segments_overlap() {
            assertThatThrownBy(() -> SimpleDailyTimeSegments.of(List.of(
                    segment(LocalTime.of(9, 0), Duration.ofHours(3)),
                    segment(LocalTime.of(11, 0), Duration.ofHours(2))
            )))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("포함 여부 테스트")
    class ContainsTest {

        @Test
        @DisplayName("하나 이상의 구간이 로컬 시각을 포함하면 true")
        void return_true_when_any_segment_contains_local_time() {
            var segments = segments();

            assertThat(segments.contains(LocalTime.of(1, 0))).isTrue();
            assertThat(segments.contains(LocalTime.of(8, 0))).isTrue();
            assertThat(segments.contains(LocalTime.of(23, 59, 59, 999_999_999))).isTrue();
        }

        @Test
        @DisplayName("어떤 구간도 로컬 시각을 포함하지 않으면 false")
        void return_false_when_no_segment_contains_local_time() {
            var segments = segments();

            assertThat(segments.contains(LocalTime.MIDNIGHT)).isFalse();
            assertThat(segments.contains(LocalTime.of(5, 0))).isFalse();
            assertThat(segments.contains(LocalTime.of(12, 0))).isFalse();
        }

        @Test
        @DisplayName("Instant는 ZoneId 기준 로컬 시각으로 변환해 포함 여부를 판단한다")
        void contains_instant_by_zone_local_time() {
            var segments = segments();
            var zoneId = ZoneId.of("Asia/Seoul");

            assertThat(segments.contains(Instant.parse("2026-05-08T23:00:00Z"), zoneId)).isTrue();
            assertThat(segments.contains(Instant.parse("2026-05-09T03:00:00Z"), zoneId)).isFalse();
        }

        @Test
        @DisplayName("포함 여부 조회 인자가 null이면 예외")
        void throw_exception_when_contains_argument_is_null() {
            var segments = segments();
            var at = Instant.parse("2026-05-08T23:00:00Z");
            var zoneId = ZoneId.of("Asia/Seoul");

            assertThatThrownBy(() -> segments.contains((LocalTime) null))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> segments.contains(null, zoneId))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> segments.contains(at, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("겹침 여부 테스트")
    class OverlapsTest {

        @Test
        @DisplayName("하나 이상의 구간이 대상 구간과 겹치면 true")
        void return_true_when_any_segment_overlaps_target_segment() {
            var segments = segments();

            assertThat(segments.overlaps(segment(LocalTime.of(11, 0), Duration.ofHours(2)))).isTrue();
        }

        @Test
        @DisplayName("어떤 구간도 대상 구간과 겹치지 않으면 false")
        void return_false_when_no_segment_overlaps_target_segment() {
            var segments = segments();

            assertThat(segments.overlaps(segment(LocalTime.of(12, 0), Duration.ofHours(1)))).isFalse();
        }

        @Test
        @DisplayName("하나 이상의 구간이 다른 DailyTimeSegments와 겹치면 true")
        void return_true_when_any_segment_overlaps_other_segments() {
            var segments = segments();
            var other = SimpleDailyTimeSegments.of(List.of(
                    segment(LocalTime.of(12, 0), Duration.ofHours(1)),
                    segment(LocalTime.of(23, 0), Duration.ofMinutes(30))
            ));

            assertThat(segments.overlaps(other)).isTrue();
        }

        @Test
        @DisplayName("어떤 구간도 다른 DailyTimeSegments와 겹치지 않으면 false")
        void return_false_when_no_segment_overlaps_other_segments() {
            var segments = segments();
            var other = SimpleDailyTimeSegments.of(List.of(
                    segment(LocalTime.of(5, 0), Duration.ofHours(3))
            ));

            assertThat(segments.overlaps(other)).isFalse();
        }

        @Test
        @DisplayName("겹침 여부 조회 인자가 null이면 예외")
        void throw_exception_when_overlaps_argument_is_null() {
            var segments = segments();

            assertThatThrownBy(() -> segments.overlaps((DailyTimeSegment) null))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> segments.overlaps((DailyTimeSegments) null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    private static SimpleDailyTimeSegments segments() {
        return SimpleDailyTimeSegments.of(List.of(
                segment(LocalTime.of(1, 0), Duration.ofHours(4)),
                segment(LocalTime.of(8, 0), Duration.ofHours(4)),
                segment(LocalTime.of(13, 0), Duration.ofHours(11))
        ));
    }

    private static DailyTimeSegment segment(LocalTime startAt, Duration duration) {
        return SimpleDailyTimeSegment.of(startAt, duration);
    }

    private static List<SimpleDailyTimeSegment> listWithNullSegment() {
        var segments = new java.util.ArrayList<SimpleDailyTimeSegment>();
        segments.add(SimpleDailyTimeSegment.of(LocalTime.of(9, 0), Duration.ofHours(1)));
        segments.add(null);
        return segments;
    }
}
