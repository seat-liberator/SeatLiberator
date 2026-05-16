package com.seatliberator.seatliberator.reservation.application.booking.contract.service;

import com.seatliberator.seatliberator.reservation.application.booking.model.ReservationOccupancyPolicy;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.criteria.ReservationRoomOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocatorFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRangeFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Default Occupancy Seat Locator Finder")
class DefaultOccupancySeatLocatorFinderTest {
    @Mock
    ReservationReader reader;

    ReservationOccupancyPolicy policy = new ReservationOccupancyPolicy();

    @InjectMocks
    DefaultOccupancySeatLocatorFinder finder;

    @Test
    @DisplayName("roomId와 시간 범위로 겹치는 예약을 조회한다")
    void find_overlapping_reservations_by_room_and_range() {
        var roomId = "room-1";
        var range = InstantRangeFixture.get();
        var criteria = ReservationRoomOverlapCriteria.of(roomId, range)
                .withFilter(ReservationFilter.empty().withStatuses(policy.occupyingStatuses()));

        when(reader.findAllOverlapping(criteria)).thenReturn(List.of());

        var result = finder.find(roomId, range);

        assertThat(result).isEmpty();
        verify(reader).findAllOverlapping(criteria);
        verifyNoMoreInteractions(reader);
    }

    @Test
    @DisplayName("점유 예약의 locator만 반환한다")
    void return_only_occupied_reservation_locators() {
        var roomId = "room-1";
        var range = InstantRangeFixture.get();
        var builder = new ReservationFixture.Builder()
                .range(range);

        var usedLocator = SeatLocatorFixture.get(roomId, "A");
        var reservedLocator = SeatLocatorFixture.get(roomId, "B");
        var canceledLocator = SeatLocatorFixture.get(roomId, "C");

        var used = builder.copy()
                .locator(usedLocator)
                .status(ReservationStatus.USED)
                .build();

        var reserved = builder.copy()
                .locator(reservedLocator)
                .status(ReservationStatus.RESERVED)
                .build();

        var criteria = ReservationRoomOverlapCriteria.of(roomId, range)
                .withFilter(ReservationFilter.empty().withStatuses(policy.occupyingStatuses()));

        when(reader.findAllOverlapping(criteria))
                .thenReturn(List.of(used, reserved));

        var result = finder.find(roomId, range);

        assertThat(result)
                .extracting(SeatLocator::key)
                .containsExactly(usedLocator.key(), reservedLocator.key());
        verify(reader).findAllOverlapping(criteria);
        verifyNoMoreInteractions(reader);
    }
}
