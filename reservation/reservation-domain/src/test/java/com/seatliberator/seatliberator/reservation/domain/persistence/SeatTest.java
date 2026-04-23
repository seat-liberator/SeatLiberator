package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.SeatStatus;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.fixture.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.fixture.SeatFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.RoomFixture.createRoom;
import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatFixture.createSeat;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Seat")
public class SeatTest {

    Clock clock = fixedClock;

    Instant now = clock.instant();

    @Test
    @DisplayName("seatId와 속한 Room의 Id로 SeatLocator를 생성한다")
    void create_seat_locator() {
        var roomId = "study-room-1";
        var seatId = "seat-A";

        var room = createRoom(roomId, now);
        var seat = new SeatFixture.Builder()
                .room(room)
                .seatId(seatId)
                .build();

        var locator = seat.getLocator();

        assertThat(locator.key()).isEqualTo(SimpleSeatLocator.of(roomId, seatId).key());
    }

    @Nested
    @DisplayName("Creation")
    class Creation {
        @Test
        @DisplayName("room과 seatId를 넘겨서 Seat 생성 가능")
        void create_with_roomId_and_seatId() {
            var room = createRoom();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, now);

            assertThat(seat.getSeatId()).isEqualTo(seatId);
            assertThat(seat.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("room이 null이면 예외")
        void throw_exception_when_room_is_null() {
            var seatId = "seat-A";
            assertThatThrownBy(() -> Seat.of(null, seatId, now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("room must not be null.");
        }

        @Test
        @DisplayName("seatId가 유효하지않으면 예외")
        void throw_exception_when_seatId_is_null() {
            var room = createRoom();

            assertThatThrownBy(() -> Seat.of(room, null, now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("seatId must not be null or blank.");

            assertThatThrownBy(() -> Seat.of(room, " ", now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("seatId must not be null or blank.");
        }

        @Test
        @DisplayName("createAt이 null이면 예외")
        void throw_exception_when_createdAt_is_null() {
            var room = createRoom();
            var seatId = "seat-A";
            assertThatThrownBy(() -> Seat.of(room, seatId, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("createdAt must not be null.");
        }

        @Test
        @DisplayName("초기 상태를 전달하지 않으면 ACTIVE로 초기화되고 lastActivatedAt은 createdAt과 같다")
        void active_when_created() {
            var room = createRoom();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, now);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.ACTIVE);
            assertThat(seat.getCreatedAt()).isEqualTo(seat.getLastActivatedAt());
        }

        @Test
        @DisplayName("INACTIVE 상태로 생성하면 lastInactivatedAt은 createdAt과 같다")
        void inactive_when_created() {
            var room = createRoom();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, SeatStatus.INACTIVE, now);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);
            assertThat(seat.getCreatedAt()).isEqualTo(seat.getLastInactivatedAt());
        }
    }

    @Nested
    @DisplayName("Update")
    class Update {
        @Test
        @DisplayName("새로운 seatId로 변경 가능")
        void update_with_new_seatId() {
            var room = createRoom();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, now);

            assertThat(seat.getSeatId()).isEqualTo(seatId);

            var newSeatId = "seat-B";
            seat.updateSeatId(newSeatId);

            assertThat(seat.getSeatId()).isEqualTo(newSeatId);
        }

        @Test
        @DisplayName("유효하지 않은 seatId를 넘기면 예외")
        void throw_exception_when_update_with_invalid_seatId() {
            var room = createRoom();
            var seatId = "seat-A";
            var seat = Seat.of(room, seatId, now);

            assertThat(seat.getSeatId()).isEqualTo(seatId);

            assertThatThrownBy(() -> seat.updateSeatId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("seatId must not be null or blank.");

            assertThatThrownBy(() -> seat.updateSeatId(" "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("seatId must not be null or blank.");

            assertThat(seat.getSeatId()).isEqualTo(seatId);
        }

        @Test
        @DisplayName("새로운 방으로 변경 가능")
        void update_with_new_room() {
            var seatId = "seat-A";
            var oldRoomId = "study-room";
            var newRoomId = "new-study-room";

            var oldRoom = new RoomFixture.Builder()
                    .roomId(oldRoomId)
                    .build();

            var newRoom = new RoomFixture.Builder()
                    .roomId(newRoomId)
                    .build();

            var seat = new SeatFixture.Builder()
                    .seatId(seatId)
                    .room(oldRoom)
                    .build();

            assertThat(seat.getRoom()).isEqualTo(oldRoom);
            assertThat(oldRoom.getSeats())
                    .extracting(Seat::getSeatId)
                    .containsExactly(seatId);

            seat.updateRoom(newRoom);

            assertThat(seat.getRoom()).isEqualTo(newRoom);

            assertThat(oldRoom.getSeats())
                    .extracting(Seat::getSeatId)
                    .doesNotContain(seatId);

            assertThat(newRoom.getSeats())
                    .extracting(Seat::getSeatId)
                    .containsExactly(seatId);
        }

        @Test
        @DisplayName("유효하지 않은 방으로 변경하면 예외")
        void throw_exception_when_update_invalid_room() {
            var seat = createSeat();

            assertThatThrownBy(() -> seat.updateRoom(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("room must not be null.");
        }

        @Test
        @DisplayName("같은 방으로 변경하면 기존 방이랑 동일")
        void ignore_when_update_same_room() {
            var seatId = "seat-A";
            var roomId = "room-1";
            var room = new RoomFixture.Builder().roomId(roomId).build();
            var seat = new SeatFixture.Builder().seatId(seatId).room(room).build();

            seat.updateRoom(room);

            assertThat(seat.getRoom()).isEqualTo(room);
            assertThat(room.getSeats())
                    .extracting(Seat::getSeatId)
                    .containsExactly(seatId);
        }
    }

    @Nested
    @DisplayName("Transition")
    class Transition {
        @Test
        @DisplayName("좌석을 비활성화 상태로 변경할 수 있다")
        void can_transition_inactive() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            var inactivatedAt = now.plusSeconds(5);
            seat.inactive(inactivatedAt);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);
        }

        @Test
        @DisplayName("비활성화된 좌석을 또 비활성화할 수 없다")
        void can_not_transition_to_inactive_from_inactive() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            assertThatThrownBy(() -> seat.inactive(now))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Can not transition to inactive from inactive");
        }

        @Test
        @DisplayName("비활성화된 좌석은 다시 활성화할 수 있다")
        void can_transition_to_active_from_inactive() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);

            var activatedAt = now.plusSeconds(5);
            seat.active(activatedAt);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.ACTIVE);
        }

        @Test
        @DisplayName("활성화된 좌석은 다시 활성화할 수 없다")
        void can_not_transition_to_active_from_active() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            assertThatThrownBy(() -> seat.active(now.plusSeconds(5)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Can not transition to active from active");
        }
    }

    @Nested
    @DisplayName("Time")
    class Time {
        @Test
        @DisplayName("좌석 비활성화 시, 비활성화 시각이 null이면 예외")
        void throw_exception_when_inactive_with_null_inactivated_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            assertThatThrownBy(() -> seat.inactive(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("inactivatedAt must not be null.");
        }

        @Test
        @DisplayName("좌석 비활성화 시, 비활성화 시각이 생성 시각보다 과거면 예외")
        void throw_exception_when_inactivated_at_is_before_than_created_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            var inactivatedAt = now.minusSeconds(5);
            assertThatThrownBy(() -> seat.inactive(inactivatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("inactivatedAt can not be earlier than createdAt.");
        }

        @Test
        @DisplayName("좌석 비활성화 시, 비활성화 시각이 직전 활성화 시각보다 과거면 예외")
        void throw_exception_when_inactivated_at_is_before_than_last_activated_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            var activatedAt = now.plusSeconds(5);
            seat.active(activatedAt);

            var inactivatedAt = activatedAt.minusSeconds(2);
            assertThatThrownBy(() -> seat.inactive(inactivatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("inactivatedAt can not be earlier than lastActivatedAt.");
        }

        @Test
        @DisplayName("비활성화 시각이 생성 시각과 같으면 허용된다")
        void can_inactive_with_inactivatedAt_same_as_createdAt() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            seat.inactive(now);

            assertThat(seat.getLastInactivatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성화된 좌석은 activatedAt을 갖는다")
        void store_activated_at_when_active_seat() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);

            var activatedAt = now.plusSeconds(5);
            seat.active(activatedAt);

            assertThat(seat.getLastActivatedAt()).isEqualTo(activatedAt);
        }

        @Test
        @DisplayName("좌석 활성화 시, activatedAt이 null이면 예외")
        void throw_exception_when_active_with_null_activated_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);

            assertThatThrownBy(() -> seat.active(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("activatedAt must not be null.");
        }

        @Test
        @DisplayName("좌석 활성화 시, activatedAt이 createdAt보다 과거면 예외")
        void throw_exception_when_activated_at_is_before_than_created_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.INACTIVE)
                    .createdAt(now)
                    .build();

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);

            var activatedAt = now.minusSeconds(5);
            assertThatThrownBy(() -> seat.active(activatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("activatedAt can not be earlier than createdAt.");
        }

        @Test
        @DisplayName("좌석 활성화 시, 활성화 시각이 직전 비활성화 시각보다 과거면 예외")
        void throw_exception_when_activated_at_is_before_than_last_inactivated_at() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            var inactivatedAt = now.plusSeconds(5);
            seat.inactive(inactivatedAt);

            var activatedAt = inactivatedAt.minusSeconds(2);
            assertThatThrownBy(() -> seat.active(activatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("activatedAt can not be earlier than lastInactivatedAt.");
        }

        @Test
        @DisplayName("활성화 시각이 생성 시각과 같으면 허용된다")
        void can_active_with_activatedAt_same_as_createdAt() {
            var seat = new SeatFixture.Builder()
                    .status(SeatStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.ACTIVE);

            seat.inactive(now);
            seat.active(now);

            assertThat(seat.getLastActivatedAt()).isEqualTo(now);
        }
    }
}