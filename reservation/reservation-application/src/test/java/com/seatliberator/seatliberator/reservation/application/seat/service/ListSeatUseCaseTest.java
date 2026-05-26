package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.ListSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatFilter;
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
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTestSupport.listSeatQuery;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTestSupport.seat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("List Seat UseCase")
public class ListSeatUseCaseTest {
    @Mock
    RoomReader roomReader;

    @Mock
    SeatReader seatReader;

    ListSeatUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new SeatQueryService(roomReader, seatReader);
    }

    @Test
    @DisplayName("roomId에 해당하는 좌석 목록을 조회한다")
    void list_seats() {
        var query = listSeatQuery();
        var roomId = query.roomId();
        var seat = seat();

        when(roomReader.existsById(roomId)).thenReturn(true);
        when(seatReader.findByFilter(any(SeatFilter.class))).thenReturn(List.of(seat));

        var result = useCase.list(query);

        var filterCaptor = ArgumentCaptor.forClass(SeatFilter.class);
        assertThat(result)
                .hasSize(1)
                .first()
                .extracting("seatId")
                .isEqualTo(seat.getId());

        verify(roomReader).existsById(roomId);
        verify(seatReader).findByFilter(filterCaptor.capture());
        assertThat(filterCaptor.getValue().roomId()).isEqualTo(roomId);
    }

    @Test
    @DisplayName("roomId에 해당하는 방이 없으면 ROOM_NOT_FOUND 예외")
    void throw_exception_when_room_not_found() {
        var query = listSeatQuery();
        var roomId = query.roomId();

        when(roomReader.existsById(roomId)).thenReturn(false);

        assertThatApplicationThrownBy(() -> useCase.list(query))
                .hasErrorCode(ReservationApplicationErrorCode.ROOM_NOT_FOUND);

        verify(roomReader).existsById(roomId);
        verify(seatReader, never()).findByFilter(any());
    }
}
