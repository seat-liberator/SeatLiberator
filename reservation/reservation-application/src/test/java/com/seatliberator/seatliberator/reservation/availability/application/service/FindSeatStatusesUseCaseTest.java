package com.seatliberator.seatliberator.reservation.availability.application.service;

import com.seatliberator.seatliberator.reservation.availability.application.model.SeatReservationStatus;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.FindSeatStatusesUseCase;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindSeatStatusesQuery;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.result.SeatStatusesResult;
import com.seatliberator.seatliberator.reservation.book.application.contract.OccupancySeatLocatorFinder;
import com.seatliberator.seatliberator.reservation.domain.fixture.SeatFixture;
import com.seatliberator.seatliberator.reservation.seat.application.port.out.SeatReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Find Seat Statuses UseCase")
public class FindSeatStatusesUseCaseTest {
    @Mock
    SeatReader seatReader;

    @Mock
    OccupancySeatLocatorFinder occupancySeatLocatorFinder;

    FindSeatStatusesUseCase useCase;

    Instant now = fixedClock.instant();

    @BeforeEach
    void run() {
        useCase = new SeatAvailabilityService(seatReader, occupancySeatLocatorFinder);
    }

    @Test
    @DisplayName("특정 시간에 방에 존재하는 좌석의 예약 가능 상태를 반환한다")
    void return_seat_statuses() {
        var roomId = "room-1";
        var range = createRange();

        var seatBuilder = new SeatFixture.Builder().createdAt(now);

        var locatorA = createLocator(roomId, "A");
        var locatorB = createLocator(roomId, "B");
        var locatorC = createLocator(roomId, "C");

        var locators = List.of(locatorA, locatorB, locatorC);

        var seats = locators.stream()
                .map(locator -> seatBuilder.copy().locator(locator).build())
                .toList();

        when(seatReader.findByRoomId(roomId))
                .thenReturn(seats);
        when(occupancySeatLocatorFinder.find(roomId, range))
                .thenReturn(List.of(locatorA));

        var query = new FindSeatStatusesQuery(roomId, range);
        var result = useCase.find(query);

        assertThat(result)
                .containsExactlyInAnyOrder(
                        SeatStatusesResult.of(locatorA, SeatReservationStatus.OCCUPIED),
                        SeatStatusesResult.of(locatorB, SeatReservationStatus.AVAILABLE),
                        SeatStatusesResult.of(locatorC, SeatReservationStatus.AVAILABLE)
                );

        verify(seatReader).findByRoomId(roomId);
        verify(occupancySeatLocatorFinder).find(roomId, range);
    }

    @Test
    @DisplayName("방에 좌석이 없으면 빈 리스트를 반환하고 예약은 조회하지 않는다")
    void return_empty_seats_and_skip_reservation_query_when_no_seats_exists_in_room() {
        var roomId = "room-1";
        var range = createRange();

        when(seatReader.findByRoomId(roomId))
                .thenReturn(List.of());

        var query = new FindSeatStatusesQuery(roomId, range);
        var result = useCase.find(query);

        assertThat(result).isEmpty();

        verify(seatReader).findByRoomId(roomId);
        verify(occupancySeatLocatorFinder, never()).find(roomId, range);
    }
}