package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.UpdateSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.UpdateSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotFixture.get;
import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update SeatTimeSlot UseCase")
public class UpdateSeatTimeSlotUseCaseTest {
    @Mock
    SeatReader seatReader;

    @Mock
    SeatTimeSlotReader seatTimeSlotReader;

    @Mock
    SeatTimeSlotStore seatTimeSlotStore;

    Clock clock = fixedClock;

    UpdateSeatTimeSlotUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new SeatTimeSlotCommandService(seatReader, seatTimeSlotReader, seatTimeSlotStore, clock);
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯의 구간을 변경한다")
    void update_seat_time_slot() {
        var seatTimeSlotId = UUID.randomUUID();
        var slot = get();
        var command = new UpdateSeatTimeSlotCommand(seatTimeSlotId, LocalTime.of(13, 0), Duration.ofHours(3));

        when(seatTimeSlotReader.findById(seatTimeSlotId)).thenReturn(Optional.of(slot));

        var result = useCase.update(command);

        verify(seatTimeSlotReader).findById(seatTimeSlotId);
        verify(seatTimeSlotStore).save(slot);
        assertThat(slot.getSlotRange().startAt()).isEqualTo(command.startAt());
        assertThat(slot.getSlotRange().duration()).isEqualTo(command.duration());
        assertThat(result.startAt()).isEqualTo(command.startAt());
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯이 없으면 SEAT_TIME_SLOT_NOT_FOUND 예외")
    void throw_exception_when_seat_time_slot_not_found() {
        var seatTimeSlotId = UUID.randomUUID();
        var command = new UpdateSeatTimeSlotCommand(seatTimeSlotId, LocalTime.of(13, 0), Duration.ofHours(3));

        when(seatTimeSlotReader.findById(seatTimeSlotId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.update(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.SEAT_TIME_SLOT_NOT_FOUND);

        verify(seatTimeSlotReader).findById(seatTimeSlotId);
        verify(seatTimeSlotStore, never()).save(any());
    }
}
