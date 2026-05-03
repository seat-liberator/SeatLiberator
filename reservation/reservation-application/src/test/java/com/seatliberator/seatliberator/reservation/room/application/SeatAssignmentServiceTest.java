package com.seatliberator.seatliberator.reservation.room.application;

import com.seatliberator.seatliberator.reservation.application.room.internal.SeatAssignmentService;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.fixture.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.persistence.Room;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Seat Assignment Service")
public class SeatAssignmentServiceTest {
    @Mock
    RoomReader roomReader;

    @Mock
    SeatReader seatReader;

    @Mock
    SeatStore seatStore;

    Clock clock = fixedClock;

    SeatAssignmentService service;

    @BeforeEach
    void run() {
        service = new SeatAssignmentService(roomReader, seatReader, seatStore, clock);
    }

    private RoomSeatPair createRoomSeatPair(String roomId, String seatId) {
        var room = new RoomFixture.Builder()
                .roomId(roomId)
                .build();
        var seat = new SeatFixture.Builder()
                .seatId(seatId)
                .room(room)
                .build();
        return new RoomSeatPair(room, seat);
    }

    private record RoomSeatPair(Room room, Seat seat) {
    }

    @Nested
    @DisplayName("Create new seat")
    class CreateNewSeat {

        @Test
        @DisplayName("roomId와 seatId로 방에 좌석을 추가한다")
        void add_seat_into_room() {

            // given
            var roomId = "study-room-1";
            var seatId = "seat-A";

            var pair = createRoomSeatPair(roomId, seatId);
            var room = pair.room();

            // when
            when(roomReader.findByRoomId(roomId)).thenReturn(Optional.of(room));
            when(seatReader.existsByLocator(any())).thenReturn(false);

            var result = service.createSeat(roomId, seatId);

            // then
            assertThat(result.getRoom().getRoomId())
                    .isEqualTo(roomId);
            assertThat(result.getSeatId())
                    .isEqualTo(seatId);

            verify(seatStore).save(any(Seat.class));
        }

        @Test
        @DisplayName("roomId에 해당하는 방이 없으면 예외")
        void throw_exception_when_room_not_found() {
            // given
            var roomId = "study-room-1";
            var seatId = "seat-A";

            // when
            when(roomReader.findByRoomId(roomId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.createSeat(roomId, seatId))
                    .isInstanceOf(ReservationApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReservationApplicationErrorCode.ROOM_NOT_FOUND);

            verify(seatStore, never()).save(any());
        }

        @Test
        @DisplayName("seatId가 중복되면 예외")
        void throw_exception_when_duplicated_seat_id() {
            // given
            var roomId = "study-room-1";
            var seatId = "seat-A";

            var pair = createRoomSeatPair(roomId, seatId);
            var room = pair.room();

            // when
            when(roomReader.findByRoomId(roomId)).thenReturn(Optional.of(room));
            when(seatReader.existsByLocator(any())).thenReturn(true);

            assertThatThrownBy(() -> service.createSeat(roomId, seatId))
                    .isInstanceOf(ReservationApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReservationApplicationErrorCode.SEAT_ALREADY_EXISTS);

            verify(seatStore, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Move seat")
    class MoveSeat {

        @Test
        @DisplayName("기존 방에 속한 좌석을 새로운 방으로 이동한다")
        void move_seat_from_old_room_to_new_room() {
            // given
            var oldRoomId = "old-room-1";
            var newRoomId = "new-room-1";
            var seatId = "seat-A";

            var oldPair = createRoomSeatPair(oldRoomId, seatId);
            var seat = oldPair.seat();

            var newRoom = new RoomFixture.Builder().roomId(newRoomId).build();

            // when
            when(roomReader.findByRoomId(newRoomId)).thenReturn(Optional.of(newRoom));
            when(seatReader.findByLocator(seat.getLocator())).thenReturn(Optional.of(seat));
            when(seatReader.existsByLocator(any())).thenReturn(false);

            var result = service.moveSeat(oldRoomId, newRoomId, seatId);

            assertThat(result.getRoom().getRoomId())
                    .isEqualTo(newRoomId);

            var captor = ArgumentCaptor.forClass(Seat.class);
            verify(seatStore).save(captor.capture());

            var saved = captor.getValue();
            assertThat(saved.getRoom().getRoomId()).isEqualTo(newRoomId);
            assertThat(saved.getSeatId()).isEqualTo(seatId);
        }

        @Test
        @DisplayName("seatId에 해당하는 좌석 없으면 예외")
        void throw_exception_when_seat_not_found() {
            // given
            var oldRoomId = "old-room-1";
            var newRoomId = "new-room-1";
            var seatId = "seat-A";

            when(seatReader.findByLocator(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.moveSeat(oldRoomId, newRoomId, seatId))
                    .isInstanceOf(ReservationApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

            verify(seatStore, never()).save(any());
        }

        @Test
        @DisplayName("새로운 방에 해당하는 방 없으면 예외")
        void throw_exception_when_new_room_not_found() {
            // given
            var oldRoomId = "old-room-1";
            var newRoomId = "new-room-1";
            var seatId = "seat-A";

            var oldPair = createRoomSeatPair(oldRoomId, seatId);
            var seat = oldPair.seat();

            // when
            when(roomReader.findByRoomId(newRoomId)).thenReturn(Optional.empty());
            when(seatReader.findByLocator(seat.getLocator())).thenReturn(Optional.of(seat));

            assertThatThrownBy(() -> service.moveSeat(oldRoomId, newRoomId, seatId))
                    .isInstanceOf(ReservationApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReservationApplicationErrorCode.ROOM_NOT_FOUND);

            verify(seatStore, never()).save(any());
        }

        @Test
        @DisplayName("기존 방과 새로운 방이 동일하면 좌석 그대로 반환")
        void fast_return_when_old_room_and_new_room_is_same() {
            // given
            var oldRoomId = "old-room-1";
            var seatId = "seat-A";
            var oldPair = createRoomSeatPair(oldRoomId, seatId);
            var seat = oldPair.seat();

            when(seatReader.findByLocator(seat.getLocator())).thenReturn(Optional.of(seat));
            var result = service.moveSeat(oldRoomId, oldRoomId, seatId);

            assertThat(result.getRoom().getRoomId())
                    .isEqualTo(oldRoomId);

            verify(seatStore, never()).save(any());
        }

        @Test
        @DisplayName("새로운 방에 seatId가 중복된 좌석이 이미 있으면 예외")
        void throw_exception_duplicated_seat_id() {
            // given
            var oldRoomId = "old-room-1";
            var newRoomId = "new-room-1";
            var seatId = "seat-A";

            var oldPair = createRoomSeatPair(oldRoomId, seatId);
            var oldSeat = oldPair.seat();

            var newPair = createRoomSeatPair(newRoomId, seatId);
            var newSeat = newPair.seat();

            when(seatReader.findByLocator(oldSeat.getLocator())).thenReturn(Optional.of(oldSeat));
            when(seatReader.existsByLocator(newSeat.getLocator())).thenReturn(true);

            assertThatThrownBy(() -> service.moveSeat(oldRoomId, newRoomId, seatId))
                    .isInstanceOf(ReservationApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReservationApplicationErrorCode.SEAT_ALREADY_EXISTS);

            verify(seatStore, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Change Seat Id")
    class ChangeSeatId {
        @Test
        @DisplayName("방에 속한 좌석의 Id를 변경할 수 있다")
        void change_seat_id() {
            // given
            var roomId = "room-1";
            var seatId = "seat-A";
            var newSeatId = "seat-B";

            var pair = createRoomSeatPair(roomId, seatId);
            var seat = pair.seat();

            // when
            when(seatReader.findByLocator(seat.getLocator())).thenReturn(Optional.of(seat));
            when(seatReader.existsByLocator(any())).thenReturn(false);
            var result = service.changeSeatId(roomId, seatId, newSeatId);

            assertThat(result.getSeatId()).isEqualTo(newSeatId);

            verify(seatStore).save(any(Seat.class));
        }

        @Test
        @DisplayName("변경할 좌석이 없으면 예외")
        void throw_exception_when_seat_not_found() {
            var roomId = "room-1";
            var seatId = "seat-A";
            var newSeatId = "seat-B";

            when(seatReader.findByLocator(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.changeSeatId(roomId, seatId, newSeatId))
                    .isInstanceOf(ReservationApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

            verify(seatStore, never()).save(any());
        }

        @Test
        @DisplayName("동일한 Id로 변경 시 좌석 그대로 반환")
        void fast_return_when_change_same_seat_id() {
            // given
            var roomId = "room-1";
            var seatId = "seat-A";

            var pair = createRoomSeatPair(roomId, seatId);
            var seat = pair.seat();

            when(seatReader.findByLocator(seat.getLocator())).thenReturn(Optional.of(seat));

            var result = service.changeSeatId(roomId, seatId, seatId);

            assertThat(result.getSeatId()).isEqualTo(seatId);

            verify(seatStore, never()).save(any());
        }

        @Test
        @DisplayName("이미 존재하는 Id로 변경 시 예외")
        void throw_exception_when_change_duplicated_id() {
            // given
            var roomId = "room-1";
            var seatId = "seat-A";
            var otherSeatId = "seat-B";

            var pair = createRoomSeatPair(roomId, seatId);
            var seat = pair.seat();
            var otherPair = createRoomSeatPair(roomId, otherSeatId);
            var otherSeat = otherPair.seat();

            when(seatReader.findByLocator(seat.getLocator())).thenReturn(Optional.of(seat));
            when(seatReader.existsByLocator(otherSeat.getLocator())).thenReturn(true);

            assertThatThrownBy(() -> service.changeSeatId(roomId, seatId, otherSeatId))
                    .isInstanceOf(ReservationApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReservationApplicationErrorCode.SEAT_ALREADY_EXISTS);

            verify(seatStore, never()).save(any());
        }
    }
}
