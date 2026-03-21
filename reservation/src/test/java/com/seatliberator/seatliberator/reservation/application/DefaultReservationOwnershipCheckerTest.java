package com.seatliberator.seatliberator.reservation.application;

import com.seatliberator.seatliberator.reservation.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.service.DefaultReservationOwnershipChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.ReservationTestFixture.INITIAL_USER_ID;
import static com.seatliberator.seatliberator.reservation.ReservationTestFixture.createReservation;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultReservationOwnershipCheckerTest {
    private final ReservationStore reservationStore = mock(ReservationStore.class);
    private final DefaultReservationOwnershipChecker checker = new DefaultReservationOwnershipChecker(reservationStore);

    @Test
    @DisplayName("예약의 userId와 요청 userId가 같으면 true를 반환한다.")
    void 예약의_userId와_요청_userId가_같으면_true를_반환한다() {
        Instant startTime = Instant.parse("2026-01-01T00:00:00Z");
        var reservation = createReservation(startTime);

        when(reservationStore.findById(1L)).thenReturn(Optional.of(reservation));

        assertTrue(checker.hasOwnership(1L, INITIAL_USER_ID));
    }

    @Test
    @DisplayName("예약의 userId와 요청 userId가 다르면 false를 반환한다.")
    void 예약의_userId와_요청_userId가_다르면_false를_반환한다() {
        Instant startTime = Instant.parse("2026-01-01T00:00:00Z");
        var reservation = createReservation(startTime);

        when(reservationStore.findById(1L)).thenReturn(Optional.of(reservation));

        var otherUserId = INITIAL_USER_ID + "-diff";
        assertFalse(checker.hasOwnership(1L, otherUserId));
    }

    @Test
    @DisplayName("예약이 존재하지 않으면 false를 반환한다.")
    void 예약이_존재하지_않으면_false를_반환한다() {
        when(reservationStore.findById(1L)).thenReturn(Optional.empty());

        assertFalse(checker.hasOwnership(1L, INITIAL_USER_ID));
    }
}
