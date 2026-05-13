package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegmentsTestSupport.segment;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SimpleDailyTimeSegments 도메인 테스트")
class SimpleDailyTimeSegmentsTest extends AbstractDailyTimeSegmentsTest<SimpleDailyTimeSegments> {
    @Override
    public SimpleDailyTimeSegments create(List<DailyTimeSegment> ranges) {
        return SimpleDailyTimeSegments.of(ranges);
    }

    @Test
    @DisplayName("DailyTimeSegments로부터 복사할 수 있다")
    void copy_from_daily_schedule() {
        var segments = SimpleDailyTimeSegments.from(create(List.of(
                segment("09:00", "12:00"),
                segment("13:00", "18:00")
        )));

        assertThat(segments.segments())
                .extracting(DailyTimeSegment::startNanoOfDay)
                .containsExactly(
                        DailyTimeSegmentsTestSupport.nanoOf("09:00"),
                        DailyTimeSegmentsTestSupport.nanoOf("13:00")
                );

        assertThatDomainThrownBy(() -> SimpleDailyTimeSegments.from(null))
                .hasNonNullMessageFor("segments");
    }
}
