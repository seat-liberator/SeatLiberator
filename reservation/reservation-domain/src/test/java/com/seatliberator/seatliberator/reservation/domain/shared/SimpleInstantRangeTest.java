package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.InstantRangeTestSupport.END_AT;
import static com.seatliberator.seatliberator.reservation.domain.shared.InstantRangeTestSupport.START_AT;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SimpleInstantRange 도메인 테스트")
public class SimpleInstantRangeTest extends AbstractInstantRangeTest<SimpleInstantRange> {
    @Override
    public SimpleInstantRange create(Instant startAt, Instant endAt) {
        return SimpleInstantRange.of(startAt, endAt);
    }

    @Test
    @DisplayName("InstantRange로부터 복사할 수 있다")
    void copy_from_instant_range() {
        var range = SimpleInstantRange.from(create(START_AT, END_AT));

        assertThat(range.startAt()).isEqualTo(START_AT);
        assertThat(range.endAt()).isEqualTo(END_AT);

        assertThatDomainThrownBy(() -> SimpleInstantRange.from(null))
                .hasNonNullMessageFor("range");
    }
}
