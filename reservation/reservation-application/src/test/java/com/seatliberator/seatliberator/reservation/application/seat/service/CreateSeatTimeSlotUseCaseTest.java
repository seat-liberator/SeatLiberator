package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.CreateSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.CreateSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocatorFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture.createSeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create SeatTimeSlot UseCase")
public class CreateSeatTimeSlotUseCaseTest extends AbstractSeatTimeSlotUseCaseTest<CreateSeatTimeSlotUseCase> {
    @Override
    CreateSeatTimeSlotUseCase init(SeatReader seatReader, SeatTimeSlotReader seatTimeSlotReader, SeatTimeSlotStore seatTimeSlotStore, Clock clock) {
        return new SeatTimeSlotCommandService(seatReader, seatTimeSlotReader, seatTimeSlotStore, clock);
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석에 시간 슬롯을 만든다")
    void create_seat_time_slot() {
        var locator = SeatLocatorFixture.get();
        var seat = createSeat();
        var command = new CreateSeatTimeSlotCommand(locator, LocalTime.of(9, 0), Duration.ofHours(2));

        when(seatReader.findByLocator(locator)).thenReturn(Optional.of(seat));

        var result = useCase.create(command);

        var slotCaptor = ArgumentCaptor.forClass(SeatTimeSlot.class);
        verify(seatReader).findByLocator(locator);
        verify(seatTimeSlotStore).save(slotCaptor.capture());

        var saved = slotCaptor.getValue();
        assertThat(saved.getSeat()).isEqualTo(seat);
        assertThat(saved.getSlotRange().startAt()).isEqualTo(command.startAt());
        assertThat(saved.getSlotRange().duration()).isEqualTo(command.duration());
        assertThat(saved.getCreatedAt()).isEqualTo(clock.instant());
        assertThat(result.startAt()).isEqualTo(command.startAt());
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석이 없으면 SEAT_NOT_FOUND 예외")
    void throw_exception_when_seat_not_found() {
        var locator = SeatLocatorFixture.get();
        var command = new CreateSeatTimeSlotCommand(locator, LocalTime.of(9, 0), Duration.ofHours(2));

        when(seatReader.findByLocator(locator)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.create(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        verify(seatReader).findByLocator(locator);
        verify(seatTimeSlotStore, never()).save(any());
    }
}
