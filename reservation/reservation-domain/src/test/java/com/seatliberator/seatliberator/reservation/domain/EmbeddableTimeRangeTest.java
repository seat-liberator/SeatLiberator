package com.seatliberator.seatliberator.reservation.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain: Embeddable Time Range")
public class EmbeddableTimeRangeTest implements TimeRangeContractTest<EmbeddableTimeRange> {

    Instant startAt = fixedClock.instant();
    Instant endAt = startAt.plusSeconds(60 * 30);

    @Override
    public EmbeddableTimeRange create(Instant startAt, Instant endAt) {
        return EmbeddableTimeRange.from(startAt, endAt);
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
        @DisplayName("정상적인 시간 범위로 생성할 수 있다")
        void create_range_when_arguments_are_valid() {
            var range = new EmbeddableTimeRange(startAt, endAt);

            assertThat(range.startAt()).isEqualTo(startAt);
            assertThat(range.endAt()).isEqualTo(endAt);
        }

        @Test
        @DisplayName("시작 시간이 null이면 예외를 던진다")
        void throw_exception_when_start_at_is_null() {
            assertThatDomainThrownBy(() -> new EmbeddableTimeRange(null, endAt))
                    .hasNonNullMessageFor("startAt");
        }

        @Test
        @DisplayName("종료 시간이 null이면 예외를 던진다")
        void throw_exception_when_end_at_is_null() {
            assertThatDomainThrownBy(() -> new EmbeddableTimeRange(startAt, null))
                    .hasNonNullMessageFor("endAt");
        }

        @Test
        @DisplayName("시작 시간이 종료 시간보다 늦으면 예외를 던진다")
        void throw_exception_when_start_at_is_after_end_at() {
            assertThatThrownBy(() -> new EmbeddableTimeRange(endAt, startAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("startAt must be before endAt");
        }

        @Test
        @DisplayName("시작 시간과 종료 시간이 같으면 예외를 던진다")
        void throw_exception_when_start_at_equals_end_at() {
            assertThatThrownBy(() -> new EmbeddableTimeRange(startAt, startAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("startAt must be before endAt");
        }

        @Test
        @DisplayName("of 팩토리로 다른 TimeRange를 복사할 수 있다")
        void copy_range_with_of_factory() {
            var source = SimpleTimeRange.of(startAt, endAt);

            var copied = EmbeddableTimeRange.of(source);

            Assertions.assertThat(copied.startAt()).isEqualTo(startAt);
            Assertions.assertThat(copied.endAt()).isEqualTo(endAt);
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
