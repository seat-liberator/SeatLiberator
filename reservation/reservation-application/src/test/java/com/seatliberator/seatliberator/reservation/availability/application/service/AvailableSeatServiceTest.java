package com.seatliberator.seatliberator.reservation.availability.application.service;

import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindAvailableSeatQuery;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.result.AvailableSeatResult;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationRoomOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import com.seatliberator.seatliberator.reservation.seat.application.port.out.SeatReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application.service: AvailableSeatService")
public class AvailableSeatServiceTest {
    @Mock
    SeatReader seatReader;

    @Mock
    ReservationReader reservationReader;

    @InjectMocks
    AvailableSeatService reader;

    Instant now;

    @BeforeEach
    void run() {
        now = fixedClock.instant();
    }

    @Test
    @DisplayName("특정 시간 및 방에서 예약할 수 있는 좌석을 반환한다")
    void return_available_seats() {
        var now = fixedClock.instant();
        var roomId = "room-1";
        var range = SimpleTimeRange.of(
                now,
                now.plusSeconds(60)
        );
        var seatALocator = SimpleSeatLocator.of(roomId, "A");
        var seatBLocator = SimpleSeatLocator.of(roomId, "B");
        var seatCLocator = SimpleSeatLocator.of(roomId, "C");

        var seatA = Seat.create(seatALocator, now);
        var seatB = Seat.create(seatBLocator, now);
        var seatC = Seat.create(seatCLocator, now);

        when(seatReader.findByRoomId(roomId))
                .thenReturn(List.of(seatA, seatB, seatC));

        var reservationA = Reservation.create(
                "user-1",
                seatALocator.roomId(),
                seatALocator.seatId(),
                range.startAt(),
                range.endAt()
        );

        var criteria = ReservationRoomOverlapCriteria.of(roomId, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED, ReservationStatus.USED));
        when(reservationReader.findAllOverlapping(criteria))
                .thenReturn(List.of(reservationA));

        var query = new FindAvailableSeatQuery(roomId, range);
        var result = reader.findAvailabilitySeats(query);

        assertThat(result)
                .extracting(AvailableSeatResult::seatId)
                .containsExactlyInAnyOrder("B", "C");

        verify(seatReader).findByRoomId(roomId);
        verify(reservationReader).findAllOverlapping(criteria);
    }

    @Test
    @DisplayName("방에 좌석이 없으면 빈 리스트를 반환하고 예약은 조회하지 않는다")
    void return_empty_seats_and_skip_reservation_query_when_no_seats_exists_in_room() {

        var now = fixedClock.instant();
        var roomId = "room-1";
        var range = SimpleTimeRange.of(
                now,
                now.plusSeconds(60)
        );

        when(seatReader.findByRoomId(roomId))
                .thenReturn(List.of());

        var query = new FindAvailableSeatQuery(roomId, range);
        var result = reader.findAvailabilitySeats(query);

        assertThat(result).isEmpty();

        verify(seatReader).findByRoomId(roomId);

        var criteria = ReservationRoomOverlapCriteria.of(roomId, range);
        verify(reservationReader, never()).findAllOverlapping(criteria);
    }

    @Test
    @DisplayName("취소된 예약은 가용 좌석 계산에서 제외한다")
    void ignore_canceled_reservation_when_calculating_available_seats() {
        var now = fixedClock.instant();
        var roomId = "room-1";
        var range = SimpleTimeRange.of(now, now.plusSeconds(60));
        var seatALocator = SimpleSeatLocator.of(roomId, "A");
        var seatA = Seat.create(seatALocator, now);

        when(seatReader.findByRoomId(roomId))
                .thenReturn(List.of(seatA));

        var criteria = ReservationRoomOverlapCriteria.of(roomId, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED, ReservationStatus.USED));
        when(reservationReader.findAllOverlapping(criteria))
                .thenReturn(List.of());

        var query = new FindAvailableSeatQuery(roomId, range);
        var result = reader.findAvailabilitySeats(query);

        assertThat(result)
                .extracting(AvailableSeatResult::seatId)
                .containsExactly("A");
    }

    @Test
    @DisplayName("만료된 예약은 가용 좌석 계산에서 제외한다")
    void ignore_expired_reservation_when_calculating_available_seats() {
        var now = fixedClock.instant();
        var roomId = "room-1";
        var range = SimpleTimeRange.of(now, now.plusSeconds(60));
        var seatALocator = SimpleSeatLocator.of(roomId, "A");
        var seatA = Seat.create(seatALocator, now);

        when(seatReader.findByRoomId(roomId))
                .thenReturn(List.of(seatA));
        var criteria = ReservationRoomOverlapCriteria.of(roomId, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED, ReservationStatus.USED));
        when(reservationReader.findAllOverlapping(criteria))
                .thenReturn(List.of());

        var query = new FindAvailableSeatQuery(roomId, range);
        var result = reader.findAvailabilitySeats(query);

        assertThat(result)
                .extracting(AvailableSeatResult::seatId)
                .containsExactly("A");
    }

    @Test
    @DisplayName("사용된 예약은 여전히 점유 좌석으로 본다")
    void treat_used_reservation_as_occupied_seat() {
        var now = fixedClock.instant();
        var roomId = "room-1";
        var range = SimpleTimeRange.of(now, now.plusSeconds(60));
        var seatALocator = SimpleSeatLocator.of(roomId, "A");
        var seatA = Seat.create(seatALocator, now);

        when(seatReader.findByRoomId(roomId))
                .thenReturn(List.of(seatA));
        var criteria = ReservationRoomOverlapCriteria.of(roomId, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED, ReservationStatus.USED));
        when(reservationReader.findAllOverlapping(criteria))
                .thenReturn(List.of(createReservation(seatALocator, range, ReservationStatus.USED)));

        var query = new FindAvailableSeatQuery(roomId, range);
        var result = reader.findAvailabilitySeats(query);

        assertThat(result).isEmpty();
    }

    private Reservation createReservation(SimpleSeatLocator locator, SimpleTimeRange range) {
        return Reservation.create(
                "user-1",
                locator.roomId(),
                locator.seatId(),
                range.startAt(),
                range.endAt(),
                ReservationStatus.RESERVED
        );
    }

    private Reservation createReservation(SimpleSeatLocator locator, SimpleTimeRange range, ReservationStatus status) {
        return Reservation.create(
                "user-1",
                locator.roomId(),
                locator.seatId(),
                range.startAt(),
                range.endAt(),
                status
        );
    }
}
