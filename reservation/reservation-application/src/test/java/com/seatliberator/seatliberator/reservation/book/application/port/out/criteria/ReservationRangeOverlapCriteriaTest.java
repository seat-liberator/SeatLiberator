package com.seatliberator.seatliberator.reservation.book.application.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Reservation Range Overlap Criteria")
public class ReservationRangeOverlapCriteriaTest {
    @Test
    @DisplayName("of는 주어진 range와 빈 filter로 criteria를 생성한다")
    void of() {
        var range = createRange();
        var criteria = ReservationRangeOverlapCriteria.of(range);

        assertThat(criteria.range()).isEqualTo(SimpleTimeRange.from(range));
        assertThat(criteria.filter()).isEqualTo(ReservationFilter.empty());
    }

    @Test
    @DisplayName("withFilter는 range는 유지하고 filter만 교체한다")
    void with_filter() {
        var range = createRange();
        var criteria = ReservationRangeOverlapCriteria.of(range);
        var filter = ReservationFilter.empty().withUserIds("user-1");

        var updated = criteria.withFilter(filter);

        assertThat(updated.range()).isEqualTo(criteria.range());
        assertThat(updated.filter()).isEqualTo(filter);
    }

    @Test
    @DisplayName("withFilter는 새로운 객체를 반환한다")
    void with_filter_returns_new_instance() {
        var range = createRange();
        var criteria = ReservationRangeOverlapCriteria.of(range);
        var filter = ReservationFilter.empty().withUserIds("user-1");

        var updated = criteria.withFilter(filter);

        assertThat(updated).isNotSameAs(criteria);
    }
}
