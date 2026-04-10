package com.seatliberator.seatliberator.reservation.book.application;

import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationExistenceChecker;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationRejectReason;
import com.seatliberator.seatliberator.reservation.book.application.service.DefaultReservationPolicyChecker;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.reservation.domain.fixture.ReservationFixture.INITIAL_USER_ID;
import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Default Reservation Policy Checker")
public class DefaultReservationPolicyCheckerTest {
    @Mock
    ReservationExistenceChecker existenceChecker;

    @InjectMocks
    DefaultReservationPolicyChecker checker;

    @Test
    @DisplayName("같은 좌석과 시간대에 예약이 이미 있으면 예약 불가와 SEAT_ALREADY_TAKEN 사유를 반환한다")
    void reject_when_seat_is_already_taken() {
        var locator = createLocator();
        var range = createRange();

        when(existenceChecker.isExistsByLocatorAndRangeAndStatus(locator, range, ReservationStatus.RESERVED)).thenReturn(true);

        var result = checker.check(INITIAL_USER_ID, locator, range);

        assertFalse(result.reservable());
        assertEquals(ReservationRejectReason.SEAT_ALREADY_TAKEN, result.rejectReason());
        verify(existenceChecker).isExistsByLocatorAndRangeAndStatus(locator, range, ReservationStatus.RESERVED);
    }

    @Test
    @DisplayName("같은 좌석과 시간대에 예약이 없으면 예약 가능 결과를 반환한다")
    void accept_when_seat_is_not_taken() {
        var locator = createLocator();
        var range = createRange();

        when(existenceChecker.isExistsByLocatorAndRangeAndStatus(locator, range, ReservationStatus.RESERVED)).thenReturn(false);

        var result = checker.check(INITIAL_USER_ID, locator, range);

        assertTrue(result.reservable());
        assertNull(result.rejectReason());
        verify(existenceChecker).isExistsByLocatorAndRangeAndStatus(locator, range, ReservationStatus.RESERVED);
    }
}
