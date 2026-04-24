package com.seatliberator.seatliberator.kernel.test.clock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Kernel Test: Test Clock")
public class TestClockTest {
    private static final Instant REFERENCE_TIME = Instant.parse("2026-01-01T00:00:00Z");

    @Nested
    @DisplayName("Get Fixed")
    class GetFixed {
        @Test
        @DisplayName("인자 없이 호출하면 기준 시각의 UTC 고정 시계를 반환한다")
        void return_fixed_clock_with_reference_time_and_utc() {
            var clock = TestClock.getFixed();

            assertThat(clock.instant()).isEqualTo(REFERENCE_TIME);
            assertThat(clock.getZone()).isEqualTo(ZoneOffset.UTC);
        }

        @Test
        @DisplayName("Instant를 전달하면 해당 시각의 UTC 고정 시계를 반환한다")
        void return_fixed_clock_with_given_instant_and_utc() {
            var instant = Instant.parse("2026-02-03T04:05:06Z");

            var clock = TestClock.getFixed(instant);

            assertThat(clock.instant()).isEqualTo(instant);
            assertThat(clock.getZone()).isEqualTo(ZoneOffset.UTC);
        }

        @Test
        @DisplayName("Instant와 ZoneOffset을 전달하면 해당 시각과 오프셋의 고정 시계를 반환한다")
        void return_fixed_clock_with_given_instant_and_offset() {
            var instant = Instant.parse("2026-02-03T04:05:06Z");
            var offset = ZoneOffset.ofHours(9);

            var clock = TestClock.getFixed(instant, offset);

            assertThat(clock.instant()).isEqualTo(instant);
            assertThat(clock.getZone()).isEqualTo(offset);
        }
    }
}
