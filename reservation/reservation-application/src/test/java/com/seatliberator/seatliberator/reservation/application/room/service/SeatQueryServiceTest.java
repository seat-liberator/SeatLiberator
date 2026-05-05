package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.port.in.FindSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.ListSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.query.FindSeatQuery;
import com.seatliberator.seatliberator.reservation.application.room.port.in.query.ListSeatQuery;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture.create;
import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Seat Query UseCase")
public class SeatQueryServiceTest {
    @Mock
    RoomReader roomReader;

    @Mock
    SeatReader seatReader;

    @Nested
    @DisplayName("List Seat UseCase")
    class ListSeatUseCaseTest {
        ListSeatUseCase useCase;

        @BeforeEach
        void run() {
            useCase = new SeatQueryService(roomReader, seatReader);
        }

        @Test
        @DisplayName("roomId에 해당하는 좌석 목록을 조회한다")
        void list_seats() {
            var room = RoomFixture.get();
            var seat = create(room, "seat-a", fixedClock.instant());
            var query = new ListSeatQuery(room.getRoomId());

            when(roomReader.existsByRoomId(room.getRoomId())).thenReturn(true);
            when(seatReader.findByRoomId(room.getRoomId())).thenReturn(List.of(seat));

            var result = useCase.list(query);

            assertThat(result)
                    .hasSize(1)
                    .first()
                    .extracting("seatId")
                    .isEqualTo(seat.getSeatId());

            verify(roomReader).existsByRoomId(room.getRoomId());
            verify(seatReader).findByRoomId(room.getRoomId());
        }

        @Test
        @DisplayName("roomId에 해당하는 방이 없으면 ROOM_NOT_FOUND 예외")
        void throw_exception_when_room_not_found() {
            var query = new ListSeatQuery("missing-room");

            when(roomReader.existsByRoomId(query.roomId())).thenReturn(false);

            assertThatThrownBy(() -> useCase.list(query))
                    .isInstanceOf(ReservationApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReservationApplicationErrorCode.ROOM_NOT_FOUND);

            verify(roomReader).existsByRoomId(query.roomId());
            verify(seatReader, never()).findByRoomId(query.roomId());
        }
    }

    @Nested
    @DisplayName("Find Seat UseCase")
    class FindSeatUseCaseTest {
        FindSeatUseCase useCase;

        @BeforeEach
        void run() {
            useCase = new SeatQueryService(roomReader, seatReader);
        }

        @Test
        @DisplayName("roomId와 seatId에 해당하는 좌석을 조회한다")
        void find_seat() {
            var room = RoomFixture.get();
            var seat = create(room, "seat-a", fixedClock.instant());
            var query = new FindSeatQuery(room.getRoomId(), seat.getSeatId());

            when(roomReader.existsByRoomId(room.getRoomId())).thenReturn(true);
            when(seatReader.findByLocator(any())).thenReturn(Optional.of(seat));

            var result = useCase.find(query);

            assertThat(result.seatId()).isEqualTo(seat.getSeatId());

            var locatorCaptor = ArgumentCaptor.forClass(SeatLocator.class);
            verify(roomReader).existsByRoomId(room.getRoomId());
            verify(seatReader).findByLocator(locatorCaptor.capture());

            var locator = locatorCaptor.getValue();
            assertThat(locator.roomId()).isEqualTo(room.getRoomId());
            assertThat(locator.seatId()).isEqualTo(seat.getSeatId());
        }

        @Test
        @DisplayName("roomId에 해당하는 방이 없으면 ROOM_NOT_FOUND 예외")
        void throw_exception_when_room_not_found() {
            var query = new FindSeatQuery("missing-room", "seat-a");

            when(roomReader.existsByRoomId(query.roomId())).thenReturn(false);

            assertThatThrownBy(() -> useCase.find(query))
                    .isInstanceOf(ReservationApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReservationApplicationErrorCode.ROOM_NOT_FOUND);

            verify(roomReader).existsByRoomId(query.roomId());
            verify(seatReader, never()).findByLocator(any());
        }

        @Test
        @DisplayName("seatId에 해당하는 좌석이 없으면 SEAT_NOT_FOUND 예외")
        void throw_exception_when_seat_not_found() {
            var room = RoomFixture.get();
            var query = new FindSeatQuery(room.getRoomId(), "missing-seat");

            when(roomReader.existsByRoomId(room.getRoomId())).thenReturn(true);
            when(seatReader.findByLocator(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.find(query))
                    .isInstanceOf(ReservationApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

            verify(roomReader).existsByRoomId(room.getRoomId());
            verify(seatReader).findByLocator(any());
        }
    }
}
