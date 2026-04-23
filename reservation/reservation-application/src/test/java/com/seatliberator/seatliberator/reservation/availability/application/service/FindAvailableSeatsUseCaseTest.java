package com.seatliberator.seatliberator.reservation.availability.application.service;

import com.seatliberator.seatliberator.reservation.availability.application.port.in.FindAvailableSeatsUseCase;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindAvailableSeatQuery;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.result.AvailableSeatResult;
import com.seatliberator.seatliberator.reservation.book.application.contract.OccupancySeatLocatorFinder;
import com.seatliberator.seatliberator.reservation.book.application.contract.OccupancySeatRangeFinder;
import com.seatliberator.seatliberator.reservation.domain.fixture.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.fixture.SeatFixture;
import com.seatliberator.seatliberator.reservation.room.application.port.out.SeatReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Find Available Seats UseCase")
public class FindAvailableSeatsUseCaseTest {
    @Mock
    SeatReader seatReader;

    @Mock
    OccupancySeatLocatorFinder occupancySeatLocatorFinder;

    @Mock
    OccupancySeatRangeFinder occupancySeatRangeFinder;

    FindAvailableSeatsUseCase useCase;

    Instant now = fixedClock.instant();

    @BeforeEach
    void run() {
        useCase = new SeatAvailabilityService(seatReader, occupancySeatLocatorFinder, occupancySeatRangeFinder);
    }

    @Test
    @DisplayName("특정 시간 및 방에서 예약할 수 있는 좌석을 반환한다")
    void return_available_seats() {
        // given
        var roomId = "room-1";
        var room = new RoomFixture.Builder().roomId(roomId).build();

        var seatBuilder = new SeatFixture.Builder().room(room).createdAt(now);
        var seatA = seatBuilder.copy().seatId("A").build();
        var seatB = seatBuilder.copy().seatId("B").build();
        var seatC = seatBuilder.copy().seatId("C").build();
        var seats = List.of(seatA, seatB, seatC);

        var range = createRange();

        // when
        when(seatReader.findByRoomId(roomId))
                .thenReturn(seats);
        when(occupancySeatLocatorFinder.find(roomId, range))
                .thenReturn(List.of(seatA.getLocator()));

        var query = new FindAvailableSeatQuery(roomId, range);
        var result = useCase.find(query);

        // then
        assertThat(result)
                .extracting(AvailableSeatResult::seatId)
                .containsExactlyInAnyOrder("B", "C");

        verify(seatReader).findByRoomId(roomId);
        verify(occupancySeatLocatorFinder).find(roomId, range);
    }

    @Test
    @DisplayName("방에 좌석이 없으면 빈 리스트를 반환하고 예약은 조회하지 않는다")
    void return_empty_seats_and_skip_reservation_query_when_no_seats_exists_in_room() {
        // given
        var roomId = "room-1";
        var room = new RoomFixture.Builder().roomId(roomId).build();
        var range = createRange();

        // when
        when(seatReader.findByRoomId(roomId))
                .thenReturn(List.of());

        var query = new FindAvailableSeatQuery(roomId, range);
        var result = useCase.find(query);

        // then
        assertThat(result).isEmpty();

        verify(seatReader).findByRoomId(roomId);
        verify(occupancySeatLocatorFinder, never()).find(roomId, range);
    }
}
