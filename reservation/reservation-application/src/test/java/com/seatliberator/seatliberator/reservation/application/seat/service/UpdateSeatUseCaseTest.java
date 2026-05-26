package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.UpdateSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.criteria.SeatLookupCriteria;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTestSupport.seat;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTestSupport.updateSeatCodeCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update Seat UseCase")
public class UpdateSeatUseCaseTest {
    @Mock
    SeatStore store;

    @Mock
    SeatReader reader;

    Clock clock;

    UpdateSeatUseCase useCase;

    @BeforeEach
    void run() {
        clock = TestClock.getFixed();
        useCase = new SeatCommandService(store, reader, clock);
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석의 코드를 변경한다")
    void update_seat_code() {
        var command = updateSeatCodeCommand();
        var seatId = command.seatId();
        var seat = seat();

        when(reader.findById(seatId)).thenReturn(Optional.of(seat));
        when(reader.existsByCriteria(any(SeatLookupCriteria.class))).thenReturn(false);
        when(store.save(seat)).thenReturn(seat);

        var result = useCase.update(command);

        var criteriaCaptor = ArgumentCaptor.forClass(SeatLookupCriteria.class);
        verify(reader).findById(seatId);
        verify(reader).existsByCriteria(criteriaCaptor.capture());
        verify(store).save(seat);

        var criteria = criteriaCaptor.getValue();
        assertThat(criteria.roomId()).isEqualTo(seat.getRoomId());
        assertThat(criteria.seatCode()).isEqualTo(command.newCode());
        assertThat(seat.getCode()).isEqualTo(command.newCode());
        assertThat(result.code()).isEqualTo(command.newCode());
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석이 없으면 SEAT_NOT_FOUND 예외")
    void throw_exception_when_seat_not_found() {
        var command = updateSeatCodeCommand();
        var seatId = command.seatId();

        when(reader.findById(seatId)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.update(command))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        verify(reader).findById(seatId);
        verify(reader, never()).existsByCriteria(any());
        verify(store, never()).save(any());
    }

    @Test
    @DisplayName("변경할 좌석 코드가 이미 있으면 SEAT_ALREADY_EXISTS 예외")
    void throw_exception_when_seat_already_exists() {
        var command = updateSeatCodeCommand();
        var seatId = command.seatId();
        var seat = seat();

        when(reader.findById(seatId)).thenReturn(Optional.of(seat));
        when(reader.existsByCriteria(any(SeatLookupCriteria.class))).thenReturn(true);

        assertThatApplicationThrownBy(() -> useCase.update(command))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_ALREADY_EXISTS);

        verify(reader).findById(seatId);
        verify(reader).existsByCriteria(any(SeatLookupCriteria.class));
        verify(store, never()).save(any());
    }
}
