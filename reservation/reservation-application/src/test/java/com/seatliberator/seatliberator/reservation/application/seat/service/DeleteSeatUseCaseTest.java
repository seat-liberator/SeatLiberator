package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.DeleteSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatStore;
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
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTestSupport.deleteSeatCommand;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTestSupport.seat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Seat UseCase")
public class DeleteSeatUseCaseTest {
    @Mock
    SeatStore store;

    @Mock
    SeatReader reader;

    Clock clock;

    DeleteSeatUseCase useCase;

    @BeforeEach
    void run() {
        clock = TestClock.getFixed();
        useCase = new SeatCommandService(store, reader, clock);
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석을 삭제한다")
    void delete_seat() {
        var command = deleteSeatCommand();
        var seatId = command.seatId();
        var seat = seat();

        when(reader.findById(seatId)).thenReturn(Optional.of(seat));

        useCase.delete(command);

        verify(reader).findById(seatId);
        verify(store).delete(seat);
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석이 없으면 SEAT_NOT_FOUND 예외")
    void throw_exception_when_seat_not_found() {
        var command = deleteSeatCommand();
        var seatId = command.seatId();

        when(reader.findById(seatId)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.delete(command))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        verify(reader).findById(seatId);
        verify(store, never()).delete(any());
    }
}
