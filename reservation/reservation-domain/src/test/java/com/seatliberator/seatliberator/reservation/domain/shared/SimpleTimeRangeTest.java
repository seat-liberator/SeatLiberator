package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain: Simple Time Range")
public class SimpleTimeRangeTest implements TimeRangeContractTest<SimpleTimeRange> {

    Instant startAt = fixedClock.instant();
    Instant endAt = startAt.plusSeconds(60 * 30);

    @Override
    public SimpleTimeRange create(Instant startAt, Instant endAt) {
        return SimpleTimeRange.of(startAt, endAt);
    }

    @Override
    public Instant getStartAt() {
        return startAt;
    }

    @Override
    public Instant getEndAt() {
        return endAt;
    }

    @Nested
    @DisplayName("Creation")
    class Creation {
        @Test
        @DisplayName("시작 시간과 종료 시간이 같으면 예외를 던진다")
        void throw_exception_when_start_at_equals_end_at() {
            assertThatThrownBy(() -> new SimpleTimeRange(startAt, startAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("startAt must be before endAt");
        }

        @Test
        @DisplayName("시작 시간이 종료 시간보다 늦으면 예외를 던진다")
        void throw_exception_when_start_at_is_after_end_at() {
            assertThatThrownBy(() -> new SimpleTimeRange(endAt, startAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("startAt must be before endAt");
        }

        @Test
        @DisplayName("from 팩토리로 생성할 수 있다")
        void create_range_with_from_factory() {
            var range = SimpleTimeRange.of(startAt, endAt);

            assertThat(range.startAt()).isEqualTo(startAt);
            assertThat(range.endAt()).isEqualTo(endAt);
        }

        @Test
        @DisplayName("다른 TimeRange로부터 동일한 구간을 복사할 수 있다")
        void copy_range_with_of_factory() {
            var source = SimpleTimeRange.of(startAt, endAt);

            var copied = SimpleTimeRange.from(source);

            assertThat(copied.startAt()).isEqualTo(startAt);
            assertThat(copied.endAt()).isEqualTo(endAt);
        }
    }

    @Nested
    @DisplayName("Query")
    class Query {
        @Test
        @DisplayName("시작 시간 이상 종료 시간 미만이면 포함한다")
        void contains_time_within_range() {
            var range = SimpleTimeRange.of(startAt, endAt);

            assertThat(range.contains(startAt)).isTrue();
            assertThat(range.contains(endAt.minusNanos(1))).isTrue();
        }

        @Test
        @DisplayName("시작 시간 이전과 종료 시간 이후는 포함하지 않는다")
        void does_not_contain_time_outside_range() {
            var range = SimpleTimeRange.of(startAt, endAt);

            assertThat(range.contains(startAt.minusNanos(1))).isFalse();
            assertThat(range.contains(endAt)).isFalse();
        }

        @Test
        @DisplayName("종료 시간 이상이면 종료된 상태다")
        void return_true_when_time_is_on_or_after_end_at() {
            var range = SimpleTimeRange.of(startAt, endAt);

            assertThat(range.isEnded(endAt.minusNanos(1))).isFalse();
            assertThat(range.isEnded(endAt)).isTrue();
            assertThat(range.isEnded(endAt.plusSeconds(1))).isTrue();
        }
    }
}
