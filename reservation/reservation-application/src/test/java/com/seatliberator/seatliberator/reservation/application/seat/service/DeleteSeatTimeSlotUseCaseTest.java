package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.DeleteSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTimeSlotTestSupport.deleteSeatTimeSlotCommand;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTimeSlotTestSupport.seatTimeSlot;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete SeatTimeSlot UseCase")
public class DeleteSeatTimeSlotUseCaseTest {
    @Mock
    SeatTimeSlotReader reader;

    @Mock
    SeatTimeSlotStore store;

    @Mock
    SeatReader seatReader;

    Clock clock;

    DeleteSeatTimeSlotUseCase useCase;

    @BeforeEach
    void run() {
        clock = TestClock.getFixed();
        useCase = new SeatTimeSlotCommandService(reader, store, seatReader, clock);
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯을 삭제한다")
    void delete_seat_time_slot() {
        var command = deleteSeatTimeSlotCommand();
        var slotId = command.seatTimeSlotId();
        var slot = seatTimeSlot();

        when(reader.findById(slotId)).thenReturn(Optional.of(slot));

        useCase.delete(command);

        verify(reader).findById(slotId);
        verify(store).delete(slot);
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯이 없으면 SEAT_TIME_SLOT_NOT_FOUND 예외")
    void throw_exception_when_seat_time_slot_not_found() {
        var command = deleteSeatTimeSlotCommand();
        var slotId = command.seatTimeSlotId();

        when(reader.findById(slotId)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.delete(command))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_TIME_SLOT_NOT_FOUND);

        verify(reader).findById(slotId);
        verify(store, never()).delete(any());
    }
}
