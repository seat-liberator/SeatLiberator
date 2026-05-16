package com.seatliberator.seatliberator.reservation.domain.reservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture.INITIAL_USER_ID;
import static com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture.createReservation;
import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain: Reservation")
public class ReservationTest {

    @Test
    @DisplayName("예약은 생성되면 RESERVED 상태와 예약 시각을 가진다")
    void create_reserved_reservation() {
        var reservedAt = fixedClock.instant();

        var reservation = Reservation.of(INITIAL_USER_ID, reservedAt);

        assertThat(reservation.getUserId()).isEqualTo(INITIAL_USER_ID);
        assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(reservation.getState().getReservedAt()).isEqualTo(reservedAt);
        assertThat(reservation.getState().getUsedAt()).isNull();
        assertThat(reservation.getState().getCancelledAt()).isNull();
        assertThat(reservation.getState().getExpiredAt()).isNull();
    }

    @Nested
    @DisplayName("Transition from RESERVED")
    class TransitionFromReserved {
        @Test
        @DisplayName("예약 상태에서 사용 처리할 수 있다")
        void use_reserved_reservation() {
            var reservation = createReservation();
            var usedAt = fixedClock.instant().plusSeconds(1);

            reservation.use(usedAt);

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.USED);
            assertThat(reservation.getState().getUsedAt()).isEqualTo(usedAt);
            assertThat(reservation.getState().getCancelledAt()).isNull();
            assertThat(reservation.getState().getExpiredAt()).isNull();
        }

        @Test
        @DisplayName("예약 상태에서 취소 처리할 수 있다")
        void cancel_reserved_reservation() {
            var reservation = createReservation();
            var cancelledAt = fixedClock.instant().plusSeconds(1);

            reservation.cancel(cancelledAt);

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.CANCELLED);
            assertThat(reservation.getState().getCancelledAt()).isEqualTo(cancelledAt);
            assertThat(reservation.getState().getUsedAt()).isNull();
            assertThat(reservation.getState().getExpiredAt()).isNull();
        }

        @Test
        @DisplayName("예약 상태에서 만료 처리할 수 있다")
        void expire_reserved_reservation() {
            var reservation = createReservation();
            var expiredAt = fixedClock.instant().plusSeconds(1);

            reservation.expire(expiredAt);

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.EXPIRED);
            assertThat(reservation.getState().getExpiredAt()).isEqualTo(expiredAt);
            assertThat(reservation.getState().getUsedAt()).isNull();
            assertThat(reservation.getState().getCancelledAt()).isNull();
        }
    }

    @Nested
    @DisplayName("Transition from USED")
    class TransitionFromUsed {
        @Test
        @DisplayName("사용 처리된 예약은 다시 사용 처리할 수 없다")
        void can_not_use_already_used_reservation() {
            var reservation = createReservation(ReservationStatus.USED);

            assertThatThrownBy(() -> reservation.use(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 사용된 예약입니다.");

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.USED);
        }

        @Test
        @DisplayName("사용 처리된 예약은 취소 처리할 수 없다")
        void can_not_cancel_used_reservation() {
            var reservation = createReservation(ReservationStatus.USED);

            assertThatThrownBy(() -> reservation.cancel(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 사용된 예약입니다.");

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.USED);
        }

        @Test
        @DisplayName("사용 처리된 예약은 만료 처리할 수 없다")
        void can_not_expire_used_reservation() {
            var reservation = createReservation(ReservationStatus.USED);

            assertThatThrownBy(() -> reservation.expire(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 사용된 예약입니다.");

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.USED);
        }
    }

    @Nested
    @DisplayName("Transition from CANCELLED")
    class TransitionFromCancelled {
        @Test
        @DisplayName("취소된 예약은 사용 처리할 수 없다")
        void can_not_use_cancelled_reservation() {
            var reservation = createReservation(ReservationStatus.CANCELLED);

            assertThatThrownBy(() -> reservation.use(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 취소된 예약입니다.");

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        }

        @Test
        @DisplayName("취소된 예약은 다시 취소 처리할 수 없다")
        void can_not_cancel_already_cancelled_reservation() {
            var reservation = createReservation(ReservationStatus.CANCELLED);

            assertThatThrownBy(() -> reservation.cancel(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 취소된 예약입니다.");

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        }

        @Test
        @DisplayName("취소된 예약은 만료 처리할 수 없다")
        void can_not_expire_cancelled_reservation() {
            var reservation = createReservation(ReservationStatus.CANCELLED);

            assertThatThrownBy(() -> reservation.expire(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 취소된 예약입니다.");

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("Transition from EXPIRED")
    class TransitionFromExpired {
        @Test
        @DisplayName("만료된 예약은 사용 처리할 수 없다")
        void can_not_use_expired_reservation() {
            var reservation = createReservation(ReservationStatus.EXPIRED);

            assertThatThrownBy(() -> reservation.use(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 만료된 예약입니다.");

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        }

        @Test
        @DisplayName("만료된 예약은 취소 처리할 수 없다")
        void can_not_cancel_expired_reservation() {
            var reservation = createReservation(ReservationStatus.EXPIRED);

            assertThatThrownBy(() -> reservation.cancel(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 만료된 예약입니다.");

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        }

        @Test
        @DisplayName("만료된 예약은 다시 만료 처리할 수 없다")
        void can_not_expire_already_expired_reservation() {
            var reservation = createReservation(ReservationStatus.EXPIRED);

            assertThatThrownBy(() -> reservation.expire(fixedClock.instant().plusSeconds(2)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 만료된 예약입니다.");

            assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        }
    }
}
