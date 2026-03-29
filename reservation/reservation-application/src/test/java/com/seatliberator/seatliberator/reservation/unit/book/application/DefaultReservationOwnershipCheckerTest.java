package com.seatliberator.seatliberator.reservation.unit.book.application;

import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.book.application.service.DefaultReservationOwnershipChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.TestFixture.INITIAL_USER_ID;
import static com.seatliberator.seatliberator.reservation.TestFixture.createReservation;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Default Reservation Ownership Checker")
public class DefaultReservationOwnershipCheckerTest {
    @Mock
    ReservationStore reservationStore;

    @InjectMocks
    DefaultReservationOwnershipChecker checker;

    @Test
    @DisplayName("예약의 userId와 요청 userId가 같으면 true를 반환한다.")
    void 예약의_userId와_요청_userId가_같으면_true를_반환한다() {
        var reservation = createReservation();

        when(reservationStore.findById(1L)).thenReturn(Optional.of(reservation));

        assertTrue(checker.hasOwnership(1L, INITIAL_USER_ID));
    }

    @Test
    @DisplayName("예약의 userId와 요청 userId가 다르면 false를 반환한다.")
    void 예약의_userId와_요청_userId가_다르면_false를_반환한다() {
        var reservation = createReservation();

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
