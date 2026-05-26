package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.CreateSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotStore;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotRangeOverlapCriteria;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTimeSlotTestSupport.createSeatTimeSlotCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create SeatTimeSlot UseCase")
public class CreateSeatTimeSlotUseCaseTest {
    @Mock
    SeatTimeSlotReader reader;

    @Mock
    SeatTimeSlotStore store;

    @Mock
    SeatReader seatReader;

    Clock clock;

    CreateSeatTimeSlotUseCase useCase;

    @BeforeEach
    void run() {
        clock = TestClock.getFixed();
        useCase = new SeatTimeSlotCommandService(reader, store, seatReader, clock);
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석에 시간 슬롯을 만든다")
    void create_seat_time_slot() {
        var command = createSeatTimeSlotCommand();
        var seatId = command.seatId();
        when(seatReader.existsById(seatId)).thenReturn(true);
        when(reader.existsByCriteria(any(SeatTimeSlotRangeOverlapCriteria.class))).thenReturn(false);

        var result = useCase.create(command);

        var slotCaptor = ArgumentCaptor.forClass(SeatTimeSlot.class);
        verify(seatReader).existsById(seatId);
        verify(store).save(slotCaptor.capture());

        var saved = slotCaptor.getValue();
        assertThat(saved.getSeatId()).isEqualTo(seatId);
        assertThat(saved.getSlotRange().startAt()).isEqualTo(command.startAt());
        assertThat(saved.getSlotRange().duration()).isEqualTo(command.duration());
        assertThat(saved.getCreatedAt()).isEqualTo(clock.instant());
        assertThat(result.startAt()).isEqualTo(command.startAt());
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석이 없으면 SEAT_NOT_FOUND 예외")
    void throw_exception_when_seat_not_found() {
        var command = createSeatTimeSlotCommand();
        var seatId = command.seatId();

        when(seatReader.existsById(seatId)).thenReturn(false);

        assertThatApplicationThrownBy(() -> useCase.create(command))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        verify(seatReader).existsById(seatId);
        verify(store, never()).save(any());
    }

    @Test
    @DisplayName("시간 슬롯 구간이 겹칠 경우 SEAT_TIME_SLOT_CONFLICT 예외")
    void throw_exception_when_conflict_slot_range() {
        var command = createSeatTimeSlotCommand();
        var seatId = command.seatId();

        when(seatReader.existsById(seatId)).thenReturn(true);
        when(reader.existsByCriteria(any(SeatTimeSlotRangeOverlapCriteria.class))).thenReturn(true);

        assertThatApplicationThrownBy(() -> useCase.create(command))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_TIME_SLOT_RANGE_CONFLICT);

        verify(seatReader).existsById(seatId);
        verify(reader).existsByCriteria(any(SeatTimeSlotRangeOverlapCriteria.class));
        verify(store, never()).save(any());
    }
}
