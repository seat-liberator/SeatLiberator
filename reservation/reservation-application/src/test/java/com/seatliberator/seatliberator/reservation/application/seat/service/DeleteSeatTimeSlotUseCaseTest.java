package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.DeleteSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.DeleteSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotFixture.get;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete SeatTimeSlot UseCase")
public class DeleteSeatTimeSlotUseCaseTest extends AbstractSeatTimeSlotUseCaseTest<DeleteSeatTimeSlotUseCase> {
    @Override
    DeleteSeatTimeSlotUseCase init(SeatReader seatReader, SeatTimeSlotReader seatTimeSlotReader, SeatTimeSlotStore seatTimeSlotStore, Clock clock) {
        return new SeatTimeSlotCommandService(seatReader, seatTimeSlotReader, seatTimeSlotStore, clock);
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯을 삭제한다")
    void delete_seat_time_slot() {
        var seatTimeSlotId = UUID.randomUUID();
        var slot = get();
        var command = new DeleteSeatTimeSlotCommand(seatTimeSlotId);

        when(seatTimeSlotReader.findById(seatTimeSlotId)).thenReturn(Optional.of(slot));

        useCase.delete(command);

        verify(seatTimeSlotReader).findById(seatTimeSlotId);
        verify(seatTimeSlotStore).delete(slot);
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯이 없으면 SEAT_TIME_SLOT_NOT_FOUND 예외")
    void throw_exception_when_seat_time_slot_not_found() {
        var seatTimeSlotId = UUID.randomUUID();
        var command = new DeleteSeatTimeSlotCommand(seatTimeSlotId);

        when(seatTimeSlotReader.findById(seatTimeSlotId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.delete(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.SEAT_TIME_SLOT_NOT_FOUND);

        verify(seatTimeSlotReader).findById(seatTimeSlotId);
        verify(seatTimeSlotStore, never()).delete(any());
    }
}
