package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.UpdateSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotStore;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotRangeOverlapCriteria;
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
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTimeSlotTestSupport.seatTimeSlot;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTimeSlotTestSupport.updateSeatTimeSlotCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update SeatTimeSlot UseCase")
public class UpdateSeatTimeSlotUseCaseTest {
    @Mock
    SeatTimeSlotReader reader;

    @Mock
    SeatTimeSlotStore store;

    @Mock
    SeatReader seatReader;

    Clock clock;

    UpdateSeatTimeSlotUseCase useCase;

    @BeforeEach
    void run() {
        clock = TestClock.getFixed();
        useCase = new SeatTimeSlotCommandService(reader, store, seatReader, clock);
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯의 구간을 변경한다")
    void update_seat_time_slot() {
        var command = updateSeatTimeSlotCommand();
        var slotId = command.seatTimeSlotId();
        var slot = seatTimeSlot();

        when(reader.findById(slotId)).thenReturn(Optional.of(slot));
        when(reader.existsByCriteria(any(SeatTimeSlotRangeOverlapCriteria.class))).thenReturn(false);
        when(store.save(slot)).thenReturn(slot);

        var result = useCase.update(command);

        var criteriaCaptor = ArgumentCaptor.forClass(SeatTimeSlotRangeOverlapCriteria.class);
        verify(reader).findById(slotId);
        verify(reader).existsByCriteria(criteriaCaptor.capture());
        verify(store).save(slot);

        var criteria = criteriaCaptor.getValue();
        assertThat(criteria.filter().seatId()).isEqualTo(slot.getSeatId());
        assertThat(criteria.range().startAt()).isEqualTo(command.startAt());
        assertThat(criteria.range().duration()).isEqualTo(command.duration());
        assertThat(slot.getSlotRange().startAt()).isEqualTo(command.startAt());
        assertThat(slot.getSlotRange().duration()).isEqualTo(command.duration());
        assertThat(result.startAt()).isEqualTo(command.startAt());
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯이 없으면 SEAT_TIME_SLOT_NOT_FOUND 예외")
    void throw_exception_when_seat_time_slot_not_found() {
        var command = updateSeatTimeSlotCommand();
        var slotId = command.seatTimeSlotId();

        when(reader.findById(slotId)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.update(command))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_TIME_SLOT_NOT_FOUND);

        verify(reader).findById(slotId);
        verify(reader, never()).existsByCriteria(any());
        verify(store, never()).save(any());
    }

    @Test
    @DisplayName("변경할 시간 슬롯 구간이 겹치면 SEAT_TIME_SLOT_CONFLICT 예외")
    void throw_exception_when_conflict_slot_range() {
        var command = updateSeatTimeSlotCommand();
        var slotId = command.seatTimeSlotId();
        var slot = seatTimeSlot();

        when(reader.findById(slotId)).thenReturn(Optional.of(slot));
        when(reader.existsByCriteria(any(SeatTimeSlotRangeOverlapCriteria.class))).thenReturn(true);

        assertThatApplicationThrownBy(() -> useCase.update(command))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_TIME_SLOT_RANGE_CONFLICT);

        verify(reader).findById(slotId);
        verify(reader).existsByCriteria(any(SeatTimeSlotRangeOverlapCriteria.class));
        verify(store, never()).save(any());
    }
}
