package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.temporal.DateRangeTestSupport.END_AT;
import static com.seatliberator.seatliberator.reservation.domain.shared.temporal.DateRangeTestSupport.START_AT;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SimpleDateRange 도메인 테스트")
public class SimpleDateRangeTest extends AbstractDateRangeTest<SimpleDateRange> {
    @Override
    public SimpleDateRange create(LocalDate startAt, LocalDate endAt) {
        return SimpleDateRange.of(startAt, endAt);
    }

    @Test
    @DisplayName("DateRange로부터 복사할 수 있다")
    void copy_from_date_range() {
        var range = SimpleDateRange.from(create(START_AT, END_AT));

        assertThat(range.startAt()).isEqualTo(START_AT);
        assertThat(range.endAt()).isEqualTo(END_AT);

        assertThatDomainThrownBy(() -> SimpleDateRange.from(null))
                .hasNonNullMessageFor("range");
    }
}
