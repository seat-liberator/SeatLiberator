package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.FindSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTestSupport.findSeatQuery;
import static com.seatliberator.seatliberator.reservation.application.seat.SeatTestSupport.seat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Find Seat UseCase")
public class FindSeatUseCaseTest {
    @Mock
    RoomReader roomReader;

    @Mock
    SeatReader seatReader;

    FindSeatUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new SeatQueryService(roomReader, seatReader);
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석을 조회한다")
    void find_seat() {
        var query = findSeatQuery();
        var seatId = query.seatId();
        var seat = seat();

        when(seatReader.findById(seatId)).thenReturn(Optional.of(seat));

        var result = useCase.find(query);

        assertThat(result.seatId()).isEqualTo(seat.getId());
        assertThat(result.code()).isEqualTo(seat.getCode());
        assertThat(result.status()).isEqualTo(seat.getStatus());
        verify(seatReader).findById(seatId);
    }

    @Test
    @DisplayName("seatId에 해당하는 좌석이 없으면 SEAT_NOT_FOUND 예외")
    void throw_exception_when_seat_not_found() {
        var query = findSeatQuery();
        var seatId = query.seatId();

        when(seatReader.findById(seatId)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.find(query))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        verify(seatReader).findById(seatId);
    }
}
