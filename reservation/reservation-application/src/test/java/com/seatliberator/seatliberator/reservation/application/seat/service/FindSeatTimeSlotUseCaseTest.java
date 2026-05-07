package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.FindSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.FindSeatTimeSlotQuery;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Find SeatTimeSlot UseCase")
public class FindSeatTimeSlotUseCaseTest extends AbstractSeatTimeSlotUseCaseTest<FindSeatTimeSlotUseCase> {
    @Override
    FindSeatTimeSlotUseCase init(SeatReader seatReader, SeatTimeSlotReader seatTimeSlotReader, SeatTimeSlotStore seatTimeSlotStore, Clock clock) {
        return new SeatTimeSlotQueryService(seatReader, seatTimeSlotReader);
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯을 조회한다")
    void find_seat_time_slot() {
        var seatTimeSlotId = UUID.randomUUID();
        var slot = get();
        var query = new FindSeatTimeSlotQuery(seatTimeSlotId);

        when(seatTimeSlotReader.findById(seatTimeSlotId)).thenReturn(Optional.of(slot));

        var result = useCase.find(query);

        assertThat(result.startAt()).isEqualTo(slot.getSlotRange().startAt());
        assertThat(result.duration()).isEqualTo(slot.getSlotRange().duration());
        verify(seatTimeSlotReader).findById(seatTimeSlotId);
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯이 없으면 SEAT_TIME_SLOT_NOT_FOUND 예외")
    void throw_exception_when_seat_time_slot_not_found() {
        var seatTimeSlotId = UUID.randomUUID();
        var query = new FindSeatTimeSlotQuery(seatTimeSlotId);

        when(seatTimeSlotReader.findById(seatTimeSlotId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.find(query))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.SEAT_TIME_SLOT_NOT_FOUND);

        verify(seatTimeSlotReader).findById(seatTimeSlotId);
    }
}
