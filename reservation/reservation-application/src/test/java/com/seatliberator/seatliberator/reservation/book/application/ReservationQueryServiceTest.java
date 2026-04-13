package com.seatliberator.seatliberator.reservation.book.application;

import com.seatliberator.seatliberator.reservation.book.application.contract.query.IdBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.contract.query.SeatBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationSeatLookupCriteria;
import com.seatliberator.seatliberator.reservation.book.application.service.ReservationQueryService;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.domain.fixture.ReservationFixture.*;
import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Reservation Query Service")
public class ReservationQueryServiceTest {
    @Mock
    ReservationReader reader;

    @InjectMocks
    ReservationQueryService service;

    @Test
    @DisplayName("reservationId 기반 locator로 예약을 조회할 수 있다")
    void read_reservation_by_id_locator_when_reservation_exists() {
        var reservation = createReservation();

        stubReservationId(reservation, 1L);

        when(reader.findById(1L)).thenReturn(Optional.of(reservation));

        var result = service.find(new IdBasedReservationLocator(1L));

        assertEquals(1L, result.reservationId());
        assertEquals(INITIAL_USER_ID, result.actorId());
        assertEquals(INITIAL_ROOM_ID, result.roomId());
        assertEquals(INITIAL_SEAT_ID, result.seatId());

        verify(reader).findById(1L);
        verify(reader, never()).findOne(any());
    }

    @Test
    @DisplayName("reservationId 기반 조회 결과가 없으면 RESERVATION_NOT_FOUND 예외를 던진다")
    void throw_not_found_exception_when_reservation_missing_for_id_locator() {
        when(reader.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(
                ReservationApplicationException.class,
                () -> service.find(new IdBasedReservationLocator(1L))
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
        var locator = reservation.getLocator();
        var locatorStartTime = range.startAt();
        var locatorEndTime = range.endAt();

        var criteria = ReservationSeatLookupCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
        when(reader.findOne(criteria))
                .thenReturn(Optional.of(reservation));

        var reservationLocator = new SeatBasedReservationLocator(
                locatorRoomId,
                locatorSeatId,
                locatorStartTime,
                locatorEndTime
        );

        var result = service.find(reservationLocator);

        assertEquals(1L, result.reservationId());
        assertEquals(INITIAL_USER_ID, result.actorId());
        assertEquals(locatorRoomId, result.roomId());
        assertEquals(locatorSeatId, result.seatId());

        verify(reader).findOne(any());
        verify(reader, never()).findById(anyLong());
    }

    @Test
    @DisplayName("seat 기반 조회 결과가 없으면 RESERVATION_NOT_FOUND 예외를 던진다")
    void throw_not_found_exception_when_reservation_missing_for_seat_locator() {
        var locator = createLocator();
        var range = createRange();

        var criteria = ReservationSeatLookupCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
        when(reader.findOne(criteria))
                .thenReturn(Optional.empty());

        var reservationLocator = new SeatBasedReservationLocator(
                locator.roomId(),
                locator.seatId(),
                range.startAt(),
                range.endAt()
        );

        var exception = assertThrows(
                ReservationApplicationException.class,
                () -> service.find(reservationLocator)
        );

        assertEquals(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND, exception.getErrorCode());
    }
}
