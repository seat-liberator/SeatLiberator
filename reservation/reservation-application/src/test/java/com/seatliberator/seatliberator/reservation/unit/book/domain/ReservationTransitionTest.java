package com.seatliberator.seatliberator.reservation.unit.book.domain;

import com.seatliberator.seatliberator.reservation.book.domain.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.reservation.TestFixture.createReservation;
import static com.seatliberator.seatliberator.reservation.TestFixture.fixedClock;
import static org.assertj.core.api.Assertions.*;

@DisplayName("Domain: Reservation Transition")
public class ReservationTransitionTest {

    @Test
    @DisplayName("에약은 사용 처리되면 USED 상태가 된다.")
    void 예약은_사용_처리되면_USED_상태가_된다() {
        var reservation = createReservation();
        var usedAt = reservation.getStartTime().plusSeconds(1);

        reservation.markUsed(usedAt);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.USED);
    }

    @Test
    @DisplayName("이미 사용된 예약은 다시 사용 처리할 수 없다.")
    void 이미_사용된_예약은_다시_사용_처리할_수_없다() {
        var reservation = createReservation();
        var usedAt = fixedClock.instant();

        reservation.markUsed(usedAt);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.USED);

        assertThatThrownBy(() -> reservation.markUsed(usedAt))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용된 예약입니다.");

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.USED);
    }

    @Test
    @DisplayName("종료 시간이 지난 예약은 사용 처리 시 EXPIRED 상태로 수렴한다.")
    void 종료_시간이_지난_예약은_사용_처리_시_EXPIRED_상태로_수렴한다() {
        var reservation = createReservation();
        var usedAt = reservation.getEndTime().plusSeconds(30);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);

        assertThatThrownBy(() -> reservation.markUsed(usedAt))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("만료된 예약입니다.");

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
    }
}
