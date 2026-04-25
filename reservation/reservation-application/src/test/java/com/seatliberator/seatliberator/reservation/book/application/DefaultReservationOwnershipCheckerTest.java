package com.seatliberator.seatliberator.reservation.book.application;

import com.seatliberator.seatliberator.reservation.application.booking.contract.service.DefaultReservationOwnershipChecker;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.domain.fixture.ReservationFixture.INITIAL_USER_ID;
import static com.seatliberator.seatliberator.reservation.domain.fixture.ReservationFixture.createReservation;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Default Reservation Ownership Checker")
public class DefaultReservationOwnershipCheckerTest {
    @Mock
    ReservationReader reader;

    @InjectMocks
    DefaultReservationOwnershipChecker checker;

    @Test
    @DisplayName("예약의 userId와 요청 userId가 같으면 true를 반환한다.")
    void return_true_when_request_user_matches_reservation_owner() {
        var reservation = createReservation();

        when(reader.findById(1L)).thenReturn(Optional.of(reservation));

        assertTrue(checker.hasOwnership(1L, INITIAL_USER_ID));
    }

    @Test
    @DisplayName("예약의 userId와 요청 userId가 다르면 false를 반환한다.")
    void return_false_when_request_user_differs_from_reservation_owner() {
        var reservation = createReservation();

        when(reader.findById(1L)).thenReturn(Optional.of(reservation));

        var otherUserId = INITIAL_USER_ID + "-diff";
        assertFalse(checker.hasOwnership(1L, otherUserId));
    }

    @Test
    @DisplayName("예약이 존재하지 않으면 false를 반환한다.")
    void return_false_when_reservation_does_not_exist() {
        when(reader.findById(1L)).thenReturn(Optional.empty());

        assertFalse(checker.hasOwnership(1L, INITIAL_USER_ID));
    }
}
