package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.ListSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.ListSeatTimeSlotQuery;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture.createSeat;
import static com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotFixture.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("List SeatTimeSlot UseCase")
public class ListSeatTimeSlotUseCaseTest extends AbstractSeatTimeSlotUseCaseTest<ListSeatTimeSlotUseCase> {
    @Override
    ListSeatTimeSlotUseCase init(SeatReader seatReader, SeatTimeSlotReader seatTimeSlotReader, SeatTimeSlotStore seatTimeSlotStore, Clock clock) {
        return new SeatTimeSlotQueryService(seatReader, seatTimeSlotReader);
    }

    @Test
    @DisplayName("seatId에 해당하는 시간 슬롯 목록을 조회한다")
    void list_seat_time_slots() {
        var seatId = UUID.randomUUID();
        var seat = createSeat();
        var slot = get();
        var query = new ListSeatTimeSlotQuery(seatId);

        when(seatReader.findById(seatId)).thenReturn(Optional.of(seat));
        when(seatTimeSlotReader.findBySeatId(seatId)).thenReturn(List.of(slot));

        var result = useCase.list(query);

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting("startAt")
                .isEqualTo(slot.getSlotRange().startAt());

        verify(seatReader).findById(seatId);
        verify(seatTimeSlotReader).findBySeatId(seatId);
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석이 없으면 SEAT_NOT_FOUND 예외")
    void throw_exception_when_seat_not_found() {
        var seatId = UUID.randomUUID();
        var query = new ListSeatTimeSlotQuery(seatId);

        when(seatReader.findById(seatId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.list(query))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        verify(seatReader).findById(seatId);
        verify(seatTimeSlotReader, never()).findBySeatId(seatId);
    }
}
