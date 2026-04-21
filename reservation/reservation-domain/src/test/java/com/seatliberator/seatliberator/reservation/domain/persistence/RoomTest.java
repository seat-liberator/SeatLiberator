package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.fixture.SeatFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Room")
public class RoomTest {

    Instant now = fixedClock.instant();

    @Test
    @DisplayName("유효한 Id와 현재 시각으로 생성한다")
    void create_with_valid_name_and_created_at() {
        var roomId = "study-room-1";

        var room = Room.of(roomId, now);

        assertThat(room.getRoomId()).isEqualTo(roomId);
        assertThat(room.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("유효하지 않은 Id 전달하면 예외")
    void throw_exception_when_invalid_name() {
        var roomId = " ";

        assertThatThrownBy(() -> Room.of(roomId, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("roomId must not be null or blank.");
    }

    @Test
    @DisplayName("유효하지 않은 현재 시각 전달하면 예외")
    void throw_exception_when_invalid_created_at() {
        var roomId = "study-room-1";

        assertThatThrownBy(() -> Room.of(roomId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("createdAt must not be null or blank.");
    }

    @Test
    @DisplayName("유효한 좌석 목록으로 생성한다")
    void create_with_valid_seat_list() {
        var roomId = "study-room-1";
        var seatBuilder = new SeatFixture.Builder().roomId(roomId);
        var seatA = seatBuilder.copy().seatId("A").build();
        var seatB = seatBuilder.copy().seatId("B").build();
        var seatC = seatBuilder.copy().seatId("C").build();
        var seats = List.of(seatA, seatB, seatC);

        var room = Room.of(roomId, seats, now);

        assertThat(room.getSeats())
                .containsExactlyInAnyOrder(seatA, seatB, seatC);
    }

    @Test
    @DisplayName("방에 속하지 않는 좌석이 있으면 예외")
    void throw_exception_when_create_with_seat_not_belong_to_room() {
        var roomId = "study-room-1";
        var diffRoomId = "diff-room-1";

        var seatBuilder = new SeatFixture.Builder();
        var seatA = seatBuilder.copy().roomId(roomId).seatId("A").build();
        var seatB = seatBuilder.copy().roomId(diffRoomId).seatId("B").build();
        var seatC = seatBuilder.copy().roomId(roomId).seatId("C").build();

        assertThatThrownBy(() -> Room.of(roomId, List.of(seatA, seatB, seatC), now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("seat must belong to room.")
                .hasMessageContaining("B");
    }

    @Test
    @DisplayName("좌석을 방에 추가할 수 있다")
    void add_seat() {
        var seatId = "A";
        var roomId = "study-room-1";
        var seat = new SeatFixture.Builder().roomId(roomId).seatId(seatId).build();
        var room = Room.of(roomId, now);

        room.addSeat(seat);

        assertThat(room.getSeats()).containsExactly(seat);
    }

    @Test
    @DisplayName("방에 속하지 않은 좌석을 추가할 수 없다")
    void throw_exception_when_add_seat_with_not_included_room() {
        var seatId = "A";
        var diffRoomId = "other-room-1";
        var roomId = "study-room-1";
        var seat = new SeatFixture.Builder().roomId(diffRoomId).seatId(seatId).build();
        var room = Room.of(roomId, now);

        assertThatThrownBy(() -> room.addSeat(seat))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("seat must belong to room.");
    }

    @Test
    @DisplayName("방 Id와 좌석 Id를 조합해서 좌석 위치를 표현한다")
    void represent_seat_locator_with_room_id_and_seat_id() {
        var seatId = "A";
        var roomId = "study-room-1";

        var seat = new SeatFixture.Builder().roomId(roomId).seatId(seatId).build();
        var room = Room.of(roomId, List.of(seat), now);

        assertThat(room.locatorOf(seat).key()).isEqualTo(SimpleSeatLocator.of(roomId, seatId).key());
    }

    @Test
    @DisplayName("방에 속하지 않은 좌석으로 위치를 만들 수 없다")
    void throw_exception_when_create_locator_with_not_included_seat() {
        var seatId = "A";
        var diffRoomId = "other-room-1";
        var roomId = "study-room-1";

        var seat = new SeatFixture.Builder().roomId(diffRoomId).seatId(seatId).build();
        var room = Room.of(roomId, now);

        assertThatThrownBy(() -> room.locatorOf(seat))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("seat must belong to room.");
    }
}