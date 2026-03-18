package reservation.domain;

import com.seatliberator.seatliberator.reservation.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.exception.ApplicationException;
import com.seatliberator.seatliberator.reservation.application.port.in.command.IdBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.application.port.in.command.SeatBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.service.DefaultReservationReader;
import com.seatliberator.seatliberator.reservation.domain.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static reservation.domain.ReservationTestFixture.*;

public class DefaultReservationReaderTest {
    private final ReservationStore reservationStore = mock(ReservationStore.class);
    private final DefaultReservationReader reservationReader = new DefaultReservationReader(reservationStore);

    @Test
    @DisplayName("reservationId 기반 locator로 예약을 조회할 수 있다")
    void reservationId_기반_locator로_예약을_조회할_수_있다() {
        Instant startTime = Instant.parse("2026-01-01T00:00:00Z");
        var reservation = createReservation(startTime);

        stubReservationId(reservation, 1L);

        when(reservationStore.findById(1L)).thenReturn(Optional.of(reservation));

        var result = reservationReader.read(new IdBasedReservationLocator(1L));

        assertEquals(1L, result.reservationId());
        assertEquals(INITIAL_USER_ID, result.actorId());
        assertEquals(INITIAL_ROOM_ID, result.roomId());
        assertEquals(INITIAL_SEAT_ID, result.seatId());

        verify(reservationStore).findById(1L);
        verify(reservationStore, never()).findReservationBySeatAt(anyString(), anyString(), any(Instant.class), any(Instant.class));
    }

    @Test
    @DisplayName("reservationId 기반 조회 결과가 없으면 RESERVATION_NOT_FOUND 예외를 던진다")
    void reservationId_기반_조회_결과가_없으면_예외를_던진다() {
        when(reservationStore.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(
                ApplicationException.class,
                () -> reservationReader.read(new IdBasedReservationLocator(1L))
        );

        assertEquals(ApplicationErrorCode.RESERVATION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("seat 기반 locator로 예약을 조회할 수 있다")
    void seat_기반_locator로_예약을_조회할_수_있다() {
        Instant startTime = Instant.parse("2026-01-01T00:00:00Z");
        var reservation = createReservation(startTime);

        stubReservationId(reservation, 1L);

        var locatorRoomId = INITIAL_ROOM_ID;
        var locatorSeatId = INITIAL_SEAT_ID;
        var locatorStartTime = reservation.getStartTime();
        var locatorEndTime = reservation.getEndTime();

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

        var result = reservationReader.read(locator);

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
    void seat_기반_조회_결과가_없으면_예외를_던진다() {
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
                ApplicationException.class,
                () -> reservationReader.read(locator)
        );

        assertEquals(ApplicationErrorCode.RESERVATION_NOT_FOUND, exception.getErrorCode());
    }

    private void stubReservationId(Reservation reservation, Long reservationId) {
        try {
            var idField = Reservation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(reservation, reservationId);
        } catch (ReflectiveOperationException e) {
            fail("테스트용 Reservation Id 설정에 실패했습니다.");
        }
    }
}
