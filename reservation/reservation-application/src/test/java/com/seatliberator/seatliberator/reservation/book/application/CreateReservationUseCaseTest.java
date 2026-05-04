package com.seatliberator.seatliberator.reservation.book.application;

import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatorCommand;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.ReservationTestSupport.createReservationCommand;
import static com.seatliberator.seatliberator.reservation.ReservationTestSupport.reservation;
import static com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture.createSeat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateReservationUseCase 테스트")
public class CreateReservationUseCaseTest extends AbstractReservationCommandServiceTest {
    @Test
    @DisplayName("예약 생성 시 좌석을 잠그고 ReservationCreator에 생성을 위임한다")
    void delegate_creation_to_reservation_creator_when_command_is_valid() {
        var command = createReservationCommand();
        var created = reservation();

        when(seatStore.findForUpdate(command.locator()))
                .thenReturn(Optional.of(createSeat()));
        when(reader.findByUserId(command.userId()))
                .thenReturn(Optional.empty());
        when(creator.create(ReservationCreatorCommand.from(command)))
                .thenReturn(created);

        service.create(command);

        verify(seatStore).findForUpdate(command.locator());
        verify(reader).findByUserId(command.userId());
        verify(creator).create(ReservationCreatorCommand.from(command));
    }

    @Test
    @DisplayName("예약 대상 좌석이 없으면 SEAT_NOT_FOUND 예외를 던진다")
    void throw_exception_when_seat_not_found() {
        var command = createReservationCommand();

        when(seatStore.findForUpdate(command.locator()))
                .thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> service.create(command))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        verify(store, never()).save(any());
        verify(creator, never()).create(any());
    }

    @Test
    @DisplayName("이미 사용자의 예약이 있으면 RESERVATION_ALREADY_EXISTS 예외를 던진다")
    void throw_exception_when_user_already_has_reservation() {
        var command = createReservationCommand();
        var existing = reservation();

        when(seatStore.findForUpdate(command.locator()))
                .thenReturn(Optional.of(createSeat()));
        when(reader.findByUserId(command.userId()))
                .thenReturn(Optional.of(existing));

        assertThatApplicationThrownBy(() -> service.create(command))
                .hasErrorCode(ReservationApplicationErrorCode.RESERVATION_ALREADY_EXISTS);

        verify(store, never()).save(any());
        verify(creator, never()).create(any());
    }
}
