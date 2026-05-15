package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyScheduleTestSupport.range;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SimpleDailySchedule 도메인 테스트")
class SimpleDailyScheduleTest extends AbstractDailyScheduleTest<SimpleDailySchedule> {
    @Override
    public SimpleDailySchedule create(List<DailyNanoRange> ranges) {
        return SimpleDailySchedule.of(ranges);
    }

    @Test
    @DisplayName("DailySchedule로부터 복사할 수 있다")
    void copy_from_daily_schedule() {
        var ranges = SimpleDailySchedule.from(create(List.of(
                range("09:00", "12:00"),
                range("13:00", "18:00")
        )));

        assertThat(ranges.ranges())
                .extracting(DailyNanoRange::startNanoOfDay)
                .containsExactly(
                        DailyScheduleTestSupport.nanoOf("09:00"),
                        DailyScheduleTestSupport.nanoOf("13:00")
                );

        assertThatDomainThrownBy(() -> SimpleDailySchedule.from(null))
                .hasNonNullMessageFor("schedule");
    }
}
