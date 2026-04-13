package com.seatliberator.seatliberator.reservation.book.application;

import com.seatliberator.seatliberator.reservation.book.application.contract.query.IdBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.contract.query.SeatBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.book.application.service.ReservationQueryService;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.domain.fixture.ReservationFixture.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Reservation Query Service")
public class ReservationReaderServiceTest {
    @Mock
    ReservationStore reservationStore;

    @InjectMocks
    ReservationQueryService reservationReader;

    @Test
    @DisplayName("reservationId 기반 locator로 예약을 조회할 수 있다")
    void read_reservation_by_id_locator_when_reservation_exists() {
        var reservation = createReservation();

        stubReservationId(reservation, 1L);

        when(reservationStore.findById(1L)).thenReturn(Optional.of(reservation));

        var result = reservationReader.find(new IdBasedReservationLocator(1L));

        assertEquals(1L, result.reservationId());
        assertEquals(INITIAL_USER_ID, result.actorId());
        assertEquals(INITIAL_ROOM_ID, result.roomId());
        assertEquals(INITIAL_SEAT_ID, result.seatId());

        verify(reservationStore).findById(1L);
        verify(reservationStore, never()).findReservationBySeatAt(anyString(), anyString(), any(Instant.class), any(Instant.class));
    }

    @Test
    @DisplayName("reservationId 기반 조회 결과가 없으면 RESERVATION_NOT_FOUND 예외를 던진다")
    void throw_not_found_exception_when_reservation_missing_for_id_locator() {
        when(reservationStore.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(
                ReservationApplicationException.class,
                () -> reservationReader.find(new IdBasedReservationLocator(1L))
        );

        assertEquals(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("seat 기반 locator로 예약을 조회할 수 있다")
    void read_reservation_by_seat_locator_when_reservation_exists() {
        var reservation = createReservation();

        stubReservationId(reservation, 1L);

        var locatorRoomId = INITIAL_ROOM_ID;
        var locatorSeatId = INITIAL_SEAT_ID;
        var range = reservation.getRange();
        var locatorStartTime = range.startAt();
        var locatorEndTime = range.endAt();

        when(reservationStore.findReservationBySeatAt(
                locatorRoomId,
                locatorSeatId,
                locatorStartTime,
                locatorEndTime
        ))
                .thenReturn(Optional.of(reservation));

        var locator = new SeatBasedReservationLocator(
                locatorRoomId,
                locatorSeatId,
                locatorStartTime,
                locatorEndTime
        );

        var result = reservationReader.find(locator);

        assertEquals(1L, result.reservationId());
        assertEquals(INITIAL_USER_ID, result.actorId());
        assertEquals(locatorRoomId, result.roomId());
        assertEquals(locatorSeatId, result.seatId());

        verify(reservationStore).findReservationBySeatAt(
                locatorRoomId,
                locatorSeatId,
                locatorStartTime,
                locatorEndTime
        );
        verify(reservationStore, never()).findById(anyLong());
    }

    @Test
    @DisplayName("seat 기반 조회 결과가 없으면 RESERVATION_NOT_FOUND 예외를 던진다")
    void throw_not_found_exception_when_reservation_missing_for_seat_locator() {
        Instant startTime = Instant.parse("2026-01-01T00:00:00Z");
        Instant endTime = startTime.plusSeconds(5);

        when(reservationStore.findReservationBySeatAt(
                INITIAL_ROOM_ID,
                INITIAL_SEAT_ID,
                startTime,
                endTime
        ))
                .thenReturn(Optional.empty());

        var locator = new SeatBasedReservationLocator(
                INITIAL_ROOM_ID,
                INITIAL_SEAT_ID,
                startTime,
                endTime
        );

        var exception = assertThrows(
                ReservationApplicationException.class,
                () -> reservationReader.find(locator)
        );

        assertEquals(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND, exception.getErrorCode());
    }
}
