package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Seat")
public class SeatTest {

    @Nested
    @DisplayName("Creation")
    class Creation {
        @Test
        @DisplayName("roomId와 seatId를 넘겨서 Seat 생성 가능")
        void create_with_roomId_and_seatId() {
            var roomId = "room-1";
            var seatId = "A";
            var createdAt = fixedClock.instant();
            var seat = Seat.create(roomId, seatId, createdAt);

            assertThat(seat.getLocator().roomId()).isEqualTo(roomId);
            assertThat(seat.getLocator().seatId()).isEqualTo(seatId);
        }

        @Test
        @DisplayName("SeatLocator를 넘겨서 Seat 생성 가능")
        void create_with_locator() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            assertThat(seat.getLocator().key()).isEqualTo(locator.key());
        }

        @Test
        @DisplayName("SeatLocator가 null이면 예외")
        void throw_exception_when_locator_is_null() {
            SeatLocator locator = null;
            var createdAt = fixedClock.instant();

            assertThatThrownBy(() -> Seat.create(locator, createdAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("locator must not be null.");
        }
    }

    @Nested
    @DisplayName("Update")
    class Update {
        @Test
        @DisplayName("새로운 roomId, seatId를 넘겨서 Locator 값 변경 가능")
        void update_with_roomId_and_seat_id() {
            var locator = createLocator("room-1", "A");
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            var newLocator = createLocator("room-2", "B");
            seat.update(newLocator.roomId(), newLocator.seatId());

            assertThat(seat.getLocator().roomId()).isEqualTo(newLocator.roomId());
            assertThat(seat.getLocator().seatId()).isEqualTo(newLocator.seatId());
            assertThat(seat.getLocator().key()).isEqualTo(newLocator.key());
        }

        @Test
        @DisplayName("null roomId를 넘기면 예외")
        void throw_exception_when_update_with_null_roomId() {
            var locator = createLocator("room-1", "A");
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            assertThatThrownBy(() -> seat.update(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("locator must not be null.");
        }

        @Test
        @DisplayName("새로운 locator를 넘겨서 값 변경 가능")
        void update_with_new_locator() {
            var locator = createLocator("room-1", "A");
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            var newLocator = createLocator("room-2", "B");
            seat.update(newLocator);

            assertThat(seat.getLocator().roomId()).isEqualTo(newLocator.roomId());
            assertThat(seat.getLocator().seatId()).isEqualTo(newLocator.seatId());
            assertThat(seat.getLocator().key()).isEqualTo(newLocator.key());
        }

        @Test
        @DisplayName("null locator를 넘기면 예외")
        void throw_exception_when_update_with_null_locator() {
            var locator = createLocator("room-1", "A");
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            assertThatThrownBy(() -> seat.update(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("locator must not be null.");
        }
    }

    @Nested
    @DisplayName("Transition")
    class Transition {
        @Test
        @DisplayName("생성 시 사용 가능 상태로 초기화된다")
        void active_when_created() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.ACTIVE);
        }

        @Test
        @DisplayName("좌석을 비활성화 상태로 변경할 수 있다")
        void can_transition_inactive() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            var inactivatedAt = createdAt.plusSeconds(5);
            seat.inactive(inactivatedAt);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);
        }

        @Test
        @DisplayName("비활성화된 좌석을 또 비활성화할 수 없다")
        void can_not_transition_to_inactive_from_inactive() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            var inactivatedAt = createdAt.plusSeconds(5);
            seat.inactive(inactivatedAt);

            assertThatThrownBy(() -> seat.inactive(inactivatedAt))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Can not transition to inactive from inactive");
        }

        @Test
        @DisplayName("비활성화된 좌석은 다시 활성화할 수 있다")
        void can_transition_to_active_from_inactive() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            var inactivatedAt = createdAt.plusSeconds(5);
            seat.inactive(inactivatedAt);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);

            var activatedAt = inactivatedAt.plusSeconds(5);
            seat.active(activatedAt);
            assertThat(seat.getStatus()).isEqualTo(SeatStatus.ACTIVE);
        }

        @Test
        @DisplayName("활성화된 좌석은 다시 활성화할 수 없다")
        void can_not_transition_to_active_from_active() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            assertThatThrownBy(() -> seat.active(createdAt.plusSeconds(5)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Can not transition to active from active");
        }
    }

    @Nested
    @DisplayName("Time")
    class Time {
        @Test
        @DisplayName("생성된 좌석은 createdAt을 갖는다")
        void store_createdAt_when_initialized() {
            var locator = createLocator();
            var activatedAt = fixedClock.instant();
            var seat = Seat.create(locator, activatedAt);

            assertThat(seat.getCreatedAt()).isEqualTo(activatedAt);
        }

        @Test
        @DisplayName("생성 시각은 null이면 예외")
        void throw_exception_when_createdAt_is_null() {
            var locator = createLocator();

            assertThatThrownBy(() -> Seat.create(locator, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("createdAt must not be null.");
        }

        @Test
        @DisplayName("ACTIVE 상태로 생성된 좌석의 activatedAt은 createdAt과 같다")
        void activated_at_is_same_as_created_at_when_initialize_to_active_state() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            assertThat(seat.getLastActivatedAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("비활성화된 좌석은 inactivatedAt을 갖는다")
        void store_inactivatedAt_when_inactive_seat() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            var inactivatedAt = createdAt.plusSeconds(5);
            seat.inactive(inactivatedAt);

            assertThat(seat.getLastInactivatedAt()).isEqualTo(inactivatedAt);
        }

        @Test
        @DisplayName("좌석 비활성화 시, 비활성화 시각이 null이면 예외")
        void throw_exception_when_inactive_with_null_inactivated_at() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            assertThatThrownBy(() -> seat.inactive(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("inactivatedAt must not be null.");
        }

        @Test
        @DisplayName("좌석 비활성화 시, 비활성화 시각이 생성 시각보다 과거면 예외")
        void throw_exception_when_inactivated_at_is_before_than_created_at() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            var inactivatedAt = createdAt.minusSeconds(5);
            assertThatThrownBy(() -> seat.inactive(inactivatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("inactivatedAt can not be earlier than createdAt.");
        }

        @Test
        @DisplayName("비활성화 시각이 생성 시각과 같으면 허용된다")
        void can_inactive_with_inactivatedAt_same_as_createdAt() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            seat.inactive(createdAt);

            assertThat(seat.getLastInactivatedAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("활성화된 좌석은 activatedAt을 갖는다")
        void store_activated_at_when_active_seat() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            var inactivatedAt = createdAt.plusSeconds(5);
            seat.inactive(inactivatedAt);

            assertThat(seat.getStatus()).isEqualTo(SeatStatus.INACTIVE);

            var activatedAt = inactivatedAt.plusSeconds(5);
            seat.active(activatedAt);

            assertThat(seat.getLastActivatedAt()).isEqualTo(activatedAt);
        }

        @Test
        @DisplayName("좌석 활성화 시, activatedAt이 null이면 예외")
        void throw_exception_when_active_with_null_activated_at() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            var inactivatedAt = createdAt.plusSeconds(5);
            seat.inactive(inactivatedAt);

            assertThatThrownBy(() -> seat.active(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("activatedAt must not be null.");
        }

        @Test
        @DisplayName("좌석 활성화 시, activatedAt이 createdAt보다 과거면 예외")
        void throw_exception_when_activated_at_is_before_than_created_at() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            var inactivatedAt = createdAt.plusSeconds(5);
            seat.inactive(inactivatedAt);

            var activatedAt = createdAt.minusSeconds(5);
            assertThatThrownBy(() -> seat.active(activatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("activatedAt can not be earlier than createdAt.");
        }

        @Test
        @DisplayName("활성화 시각이 생성 시각과 같으면 허용된다")
        void can_active_with_activatedAt_same_as_createdAt() {
            var locator = createLocator();
            var createdAt = fixedClock.instant();
            var seat = Seat.create(locator, createdAt);

            seat.inactive(createdAt);
            seat.active(createdAt);

            assertThat(seat.getLastActivatedAt()).isEqualTo(createdAt);
        }
    }
}