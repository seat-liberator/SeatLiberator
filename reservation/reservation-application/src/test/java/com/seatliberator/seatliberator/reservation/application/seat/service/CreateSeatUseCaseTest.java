package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.CreateSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.criteria.SeatLookupCriteria;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTestSupport.createSeatCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create Seat UseCase")
public class CreateSeatUseCaseTest {
    @Mock
    SeatStore store;

    @Mock
    SeatReader reader;

    Clock clock;

    CreateSeatUseCase useCase;

    @BeforeEach
    void run() {
        clock = TestClock.getFixed();
        useCase = new SeatCommandService(store, reader, clock);
    }

    @Test
    @DisplayName("roomId에 해당하는 방에 좌석을 만든다")
    void create_seat() {
        var command = createSeatCommand();

        when(reader.existsByCriteria(any(SeatLookupCriteria.class))).thenReturn(false);
        when(store.save(any(Seat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.create(command);

        var criteriaCaptor = ArgumentCaptor.forClass(SeatLookupCriteria.class);
        var seatCaptor = ArgumentCaptor.forClass(Seat.class);
        verify(reader).existsByCriteria(criteriaCaptor.capture());
        verify(store).save(seatCaptor.capture());

        var criteria = criteriaCaptor.getValue();
        assertThat(criteria.roomId()).isEqualTo(command.roomId());
        assertThat(criteria.seatCode()).isEqualTo(command.seatCode());

        var saved = seatCaptor.getValue();
        assertThat(saved.getRoomId()).isEqualTo(command.roomId());
        assertThat(saved.getCode()).isEqualTo(command.seatCode());
        assertThat(saved.getCreatedAt()).isEqualTo(clock.instant());
        assertThat(result.code()).isEqualTo(command.seatCode());
        assertThat(result.createdAt()).isEqualTo(clock.instant());
    }

    @Test
    @DisplayName("같은 방에 같은 좌석 코드가 있으면 SEAT_ALREADY_EXISTS 예외")
    void throw_exception_when_seat_already_exists() {
        var command = createSeatCommand();

        when(reader.existsByCriteria(any(SeatLookupCriteria.class))).thenReturn(true);

        assertThatApplicationThrownBy(() -> useCase.create(command))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_ALREADY_EXISTS);

        verify(reader).existsByCriteria(any(SeatLookupCriteria.class));
        verify(store, never()).save(any());
    }
}
