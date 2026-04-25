package com.seatliberator.seatliberator.reservation.book.application;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationSeatOverlapCriteria;
import com.seatliberator.seatliberator.reservation.book.application.service.ReservationCommandService;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;
import com.seatliberator.seatliberator.reservation.room.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatFixture.createSeat;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Reservation Command Service")
public class ReservationCommandServiceTest {
    @Mock
    ReservationStore reservationStore;

    @Mock
    ReservationReader reservationReader;

    @Mock
    SeatStore seatStore;

    ReservationCommandService service;

    @BeforeEach
    void setUp() {
        service = new ReservationCommandService(reservationStore, reservationReader, seatStore, fixedClock);
    }

    @Test
    @DisplayName("예약 생성 시 좌석을 잠그고 예약을 저장한 뒤 결과를 반환한다")
    void save_reservation_when_command_is_valid() {
        var command = createCommand();

        when(seatStore.findForUpdate(command.roomId(), command.seatId()))
                .thenReturn(Optional.of(createSeat()));
        when(reservationReader.findByUserId(command.userId()))
                .thenReturn(Optional.empty());
        when(reservationReader.existsOverlapping(any(ReservationSeatOverlapCriteria.class)))
                .thenReturn(false);
        when(reservationStore.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(command);

        assertThat(result.actorId()).isEqualTo(command.userId());
        assertThat(result.roomId()).isEqualTo(command.roomId());
        assertThat(result.seatId()).isEqualTo(command.seatId());

        var reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationStore).save(reservationCaptor.capture());
        var saved = reservationCaptor.getValue();
        assertThat(saved.getUserId()).isEqualTo(command.userId());
        assertThat(saved.getLocator().roomId()).isEqualTo(command.roomId());
        assertThat(saved.getLocator().seatId()).isEqualTo(command.seatId());
    }

    @Test
    @DisplayName("예약 대상 좌석이 없으면 SEAT_NOT_FOUND 예외를 던진다")
    void throw_exception_when_seat_not_found() {
        var command = createCommand();

        when(seatStore.findForUpdate(command.roomId(), command.seatId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        verify(reservationStore, never()).save(any());
    }

    @Test
    @DisplayName("이미 사용자의 예약이 있으면 RESERVATION_ALREADY_EXISTS 예외를 던진다")
    void throw_exception_when_user_already_has_reservation() {
        var command = createCommand();
        var existing = Reservation.create(
                command.userId(),
                command.roomId(),
                command.seatId(),
                command.startTime(),
                command.endTime()
        );

        when(seatStore.findForUpdate(command.roomId(), command.seatId()))
                .thenReturn(Optional.of(createSeat()));
        when(reservationReader.findByUserId(command.userId()))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.RESERVATION_ALREADY_EXISTS);

        verify(reservationStore, never()).save(any());
    }

    @Test
    @DisplayName("예약 시간이 겹치면 RESERVATION_TIME_CONFLICT 예외를 던진다")
    void throw_exception_when_reservation_time_conflicts() {
        var command = createCommand();

        when(seatStore.findForUpdate(command.roomId(), command.seatId()))
                .thenReturn(Optional.of(createSeat()));
        when(reservationReader.findByUserId(command.userId()))
                .thenReturn(Optional.empty());
        when(reservationReader.existsOverlapping(any(ReservationSeatOverlapCriteria.class)))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.RESERVATION_TIME_CONFLICT);

        verify(reservationStore, never()).save(any());
    }

    private CreateReservationCommand createCommand() {
        return new CreateReservationCommand(
                "user-1",
                "room-1",
                "seat-a",
                fixedClock.instant().plusSeconds(60),
                fixedClock.instant().plusSeconds(120)
        );
    }
}
