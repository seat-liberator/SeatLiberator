package com.seatliberator.seatliberator.reservation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.ReservationTestFixture.createReservation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReservationTransitionTest {

    @Test
    @DisplayName("에약은 사용 처리되면 USED 상태가 된다.")
    void 예약은_사용_처리되면_USED_상태가_된다() {
        Instant startTime = Instant.now();
        var reservation = createReservation(startTime);

        reservation.markUsed();

        assertEquals(ReservationStatus.USED, reservation.getStatus());
    }

    @Test
    @DisplayName("이미 사용된 예약은 다시 사용 처리할 수 없다.")
    void 이미_사용된_예약은_다시_사용_처리할_수_없다() {
        Instant startTime = Instant.now();
        var reservation = createReservation(startTime);

        reservation.markUsed();

        var exception = assertThrows(IllegalStateException.class, reservation::markUsed);

        assertEquals("이미 사용된 예약입니다.", exception.getMessage());
        assertEquals(ReservationStatus.USED, reservation.getStatus());
    }

    @Test
    @DisplayName("종료 시간이 지난 예약은 사용 처리 시 EXPIRED 상태로 수렴한다.")
    void 종료_시간이_지난_예약은_사용_처리_시_EXPIRED_상태로_수렴한다() {
        Instant startTime = Instant.parse("2026-01-01T00:00:00Z");
        var reservation = createReservation(startTime);

        var exception = assertThrows(IllegalStateException.class, reservation::markUsed);

        assertEquals("만료된 예약입니다.", exception.getMessage());
        assertEquals(ReservationStatus.EXPIRED, reservation.getStatus());
    }
}
