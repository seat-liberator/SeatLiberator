package com.seatliberator.seatliberator.reservation.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.TestFixture.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain: Embeddable Time Range")
public class EmbeddableTimeRangeTest {

    Instant startAt = fixedClock.instant();
    Instant endAt = startAt.plusSeconds(60 * 30);

    @Nested
    @DisplayName("Creation")
    class Creation {
        @Test
        @DisplayName("정상적인 시간 범위로 생성할 수 있다")
        void create_range_when_arguments_are_valid() {
            var range = new EmbeddableTimeRange(startAt, endAt);

            assertThat(range.startAt()).isEqualTo(startAt);
            assertThat(range.endAt()).isEqualTo(endAt);
        }

        @Test
        @DisplayName("시작 시간이 null이면 예외를 던진다")
        void throw_exception_when_start_at_is_null() {
            assertThatThrownBy(() -> new EmbeddableTimeRange(null, endAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("startAt must not be null.");
        }

        @Test
        @DisplayName("종료 시간이 null이면 예외를 던진다")
        void throw_exception_when_end_at_is_null() {
            assertThatThrownBy(() -> new EmbeddableTimeRange(startAt, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("endAt must not be null.");
        }

        @Test
        @DisplayName("시작 시간이 종료 시간보다 늦으면 예외를 던진다")
        void throw_exception_when_start_at_is_after_end_at() {
            assertThatThrownBy(() -> new EmbeddableTimeRange(endAt, startAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("startAt must be before endAt");
        }

        @Test
        @DisplayName("of 팩토리로 다른 TimeRange를 복사할 수 있다")
        void copy_range_with_of_factory() {
            var source = SimpleTimeRange.from(startAt, endAt);

            var copied = EmbeddableTimeRange.of(source);

            assertThat(copied.startAt()).isEqualTo(startAt);
            assertThat(copied.endAt()).isEqualTo(endAt);
        }
    }

    @Nested
    @DisplayName("Update")
    class Update {
        @Test
        @DisplayName("setRange로 시간 범위를 변경할 수 있다")
        void update_range_with_set_range() {
            var range = new EmbeddableTimeRange(startAt, endAt);
            var newStartAt = startAt.plusSeconds(60);
            var newEndAt = endAt.plusSeconds(60);

            range.setRange(newStartAt, newEndAt);

            assertThat(range.startAt()).isEqualTo(newStartAt);
            assertThat(range.endAt()).isEqualTo(newEndAt);
        }
    }
}
