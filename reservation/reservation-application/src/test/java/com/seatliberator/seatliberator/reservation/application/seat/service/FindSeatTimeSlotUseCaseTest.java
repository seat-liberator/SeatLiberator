package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.FindSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTimeSlotTestSupport.findSeatTimeSlotQuery;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTimeSlotTestSupport.seatTimeSlot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Find SeatTimeSlot UseCase")
public class FindSeatTimeSlotUseCaseTest {
    @Mock
    SeatTimeSlotReader reader;

    @Mock
    SeatReader seatReader;

    FindSeatTimeSlotUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new SeatTimeSlotQueryService(seatReader, reader);
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯을 조회한다")
    void find_seat_time_slot() {
        var query = findSeatTimeSlotQuery();
        var slotId = query.seatTimeSlotId();
        var slot = seatTimeSlot();

        when(reader.findById(slotId)).thenReturn(Optional.of(slot));

        var result = useCase.find(query);

        assertThat(result.startAt()).isEqualTo(slot.getSlotRange().startAt());
        assertThat(result.duration()).isEqualTo(slot.getSlotRange().duration());
        assertThat(result.slotStatus()).isEqualTo(slot.getSlotStatus());
        verify(reader).findById(slotId);
    }

    @Test
    @DisplayName("seatTimeSlotId에 해당하는 시간 슬롯이 없으면 SEAT_TIME_SLOT_NOT_FOUND 예외")
    void throw_exception_when_seat_time_slot_not_found() {
        var query = findSeatTimeSlotQuery();
        var slotId = query.seatTimeSlotId();

        when(reader.findById(slotId)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.find(query))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_TIME_SLOT_NOT_FOUND);

        verify(reader).findById(slotId);
    }
}
