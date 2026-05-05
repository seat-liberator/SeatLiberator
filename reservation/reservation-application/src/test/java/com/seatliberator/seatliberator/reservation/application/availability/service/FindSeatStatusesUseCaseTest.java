package com.seatliberator.seatliberator.reservation.application.availability.service;

import com.seatliberator.seatliberator.reservation.application.availability.model.SeatReservationStatus;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.FindSeatStatusesUseCase;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.query.FindSeatStatusesQuery;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.result.SeatStatusesResult;
import com.seatliberator.seatliberator.reservation.application.booking.contract.OccupancySeatLocatorFinder;
import com.seatliberator.seatliberator.reservation.application.booking.contract.OccupancySeatRangeFinder;
import com.seatliberator.seatliberator.reservation.application.room.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.shared.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Find Seat Statuses UseCase")
public class FindSeatStatusesUseCaseTest {
    @Mock
    SeatReader seatReader;

    @Mock
    OccupancySeatLocatorFinder occupancySeatLocatorFinder;

    @Mock
    OccupancySeatRangeFinder occupancySeatRangeFinder;

    FindSeatStatusesUseCase useCase;

    Instant now = fixedClock.instant();

    @BeforeEach
    void run() {
        useCase = new SeatAvailabilityService(seatReader, occupancySeatLocatorFinder, occupancySeatRangeFinder);
    }

    @Test
    @DisplayName("특정 시간에 방에 존재하는 좌석의 예약 가능 상태를 반환한다")
    void return_seat_statuses() {
        // given
        var roomId = "room-1";
        var room = new RoomFixture.Builder().roomId(roomId).build();

        var seatBuilder = new SeatFixture.Builder().room(room).createdAt(now);
        var seatA = seatBuilder.copy().seatId("A").build();
        var seatB = seatBuilder.copy().seatId("B").build();
        var seatC = seatBuilder.copy().seatId("C").build();
        var seats = List.of(seatA, seatB, seatC);

        var range = createRange();

        when(seatReader.findByRoomId(roomId))
                .thenReturn(seats);
        when(occupancySeatLocatorFinder.find(roomId, range))
                .thenReturn(List.of(seatA.getLocator()));

        var query = new FindSeatStatusesQuery(roomId, range);
        var result = useCase.find(query);

        assertThat(result)
                .containsExactlyInAnyOrder(
                        SeatStatusesResult.of(seatA.getLocator(), SeatReservationStatus.OCCUPIED),
                        SeatStatusesResult.of(seatB.getLocator(), SeatReservationStatus.AVAILABLE),
                        SeatStatusesResult.of(seatC.getLocator(), SeatReservationStatus.AVAILABLE)
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