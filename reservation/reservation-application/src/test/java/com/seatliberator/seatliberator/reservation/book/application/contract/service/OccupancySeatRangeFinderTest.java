package com.seatliberator.seatliberator.reservation.book.application.contract.service;

import com.seatliberator.seatliberator.reservation.book.application.contract.OccupancySeatRangeFinder;
import com.seatliberator.seatliberator.reservation.book.application.model.ReservationOccupancyPolicy;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationSeatOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.fixture.ReservationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Occupancy Seat Range Finder")
public class OccupancySeatRangeFinderTest {
    @Mock
    ReservationReader reader;

    OccupancySeatRangeFinder finder;

    ReservationOccupancyPolicy policy = new ReservationOccupancyPolicy();

    @BeforeEach
    void run() {
        finder = new DefaultOccupancySeatRangeFinder(reader);
    }

    @Test
    @DisplayName("targetLocator에 해당하는 좌석이 targetRange 구간 내 점유된 구간을 조회한다")
    void find_occupied_range_by_target_locator_and_range() {
        var locator = createLocator();
        var range = createRange();

        var criteria = ReservationSeatOverlapCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(policy.occupyingStatuses()));
        when(reader.findAllOverlapping(criteria)).thenReturn(List.of());

        var result = finder.find(locator, range);

        assertThat(result).isEmpty();
        verify(reader).findAllOverlapping(criteria);
        verifyNoMoreInteractions(reader);
    }

    @Test
    @DisplayName("점유 구간의 range만 반환한다")
    void return_only_occupied_range() {
        var locator = createLocator();
        var builder = new ReservationFixture.Builder()
                .locator(locator);

        var usedAt = fixedClock.instant();
        var used = builder.copy()
                .range(createRange(usedAt))
                .status(ReservationStatus.USED)
                .build();

        var reservedAt = usedAt.plusSeconds(3600);
        var reserved = builder.copy()
                .range(createRange(reservedAt))
                .status(ReservationStatus.RESERVED)
                .build();

        var targetRange = createRange(usedAt, reservedAt);
        var criteria = ReservationSeatOverlapCriteria.of(locator, targetRange)
                .withFilter(ReservationFilter.empty().withStatuses(policy.occupyingStatuses()));
        when(reader.findAllOverlapping(criteria))
                .thenReturn(List.of(used, reserved));

        var result = finder.find(locator, targetRange);

        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(used.getRange(), reserved.getRange());
        verify(reader).findAllOverlapping(criteria);
        verifyNoMoreInteractions(reader);
    }
}
