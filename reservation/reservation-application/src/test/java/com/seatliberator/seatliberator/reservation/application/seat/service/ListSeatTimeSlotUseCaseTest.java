package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.ListSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotFilter;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTimeSlotTestSupport.listSeatTimeSlotQuery;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTimeSlotTestSupport.seatTimeSlot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("List SeatTimeSlot UseCase")
public class ListSeatTimeSlotUseCaseTest {
    @Mock
    SeatTimeSlotReader reader;

    @Mock
    SeatReader seatReader;

    ListSeatTimeSlotUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new SeatTimeSlotQueryService(seatReader, reader);
    }

    @Test
    @DisplayName("seatId에 해당하는 시간 슬롯 목록을 조회한다")
    void list_seat_time_slots() {
        var query = listSeatTimeSlotQuery();
        var seatId = query.seatId();
        var slot = seatTimeSlot(seatId);

        when(seatReader.existsById(seatId)).thenReturn(true);
        when(reader.findByFilter(any(SeatTimeSlotFilter.class))).thenReturn(List.of(slot));

        var result = useCase.list(query);

        var filterCaptor = ArgumentCaptor.forClass(SeatTimeSlotFilter.class);
        assertThat(result)
                .hasSize(1)
                .first()
                .extracting("startAt")
                .isEqualTo(slot.getSlotRange().startAt());

        verify(seatReader).existsById(seatId);
        verify(reader).findByFilter(filterCaptor.capture());
        assertThat(filterCaptor.getValue().seatId()).isEqualTo(seatId);
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석이 없으면 SEAT_NOT_FOUND 예외")
    void throw_exception_when_seat_not_found() {
        var query = listSeatTimeSlotQuery();
        var seatId = query.seatId();

        when(seatReader.existsById(seatId)).thenReturn(false);

        assertThatApplicationThrownBy(() -> useCase.list(query))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        verify(seatReader).existsById(seatId);
        verify(reader, never()).findByFilter(any());
    }
}
