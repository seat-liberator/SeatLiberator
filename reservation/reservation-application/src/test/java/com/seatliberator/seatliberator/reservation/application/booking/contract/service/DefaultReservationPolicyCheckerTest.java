package com.seatliberator.seatliberator.reservation.application.booking.contract.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationRejectReason;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationSeatLookupCriteria;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.InstantRangeFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture.INITIAL_USER_ID;
import static com.seatliberator.seatliberator.reservation.domain.shared.SeatLocatorFixture.createLocator;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Default Reservation Policy Checker")
public class DefaultReservationPolicyCheckerTest {
    @Mock
    ReservationReader reader;

    @InjectMocks
    DefaultReservationPolicyChecker checker;

    @Test
    @DisplayName("같은 좌석과 시간대에 예약이 이미 있으면 예약 불가와 SEAT_ALREADY_TAKEN 사유를 반환한다")
    void reject_when_seat_is_already_taken() {
        var locator = createLocator();
        var range = InstantRangeFixture.get();

        var criteria = ReservationSeatLookupCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
        when(reader.existsOne(criteria)).thenReturn(true);

        var result = checker.check(INITIAL_USER_ID, locator, range);

        assertFalse(result.reservable());
        assertEquals(ReservationRejectReason.SEAT_ALREADY_TAKEN, result.rejectReason());
        verify(reader).existsOne(criteria);
    }

    @Test
    @DisplayName("같은 좌석과 시간대에 예약이 없으면 예약 가능 결과를 반환한다")
    void accept_when_seat_is_not_taken() {
        var locator = createLocator();
        var range = InstantRangeFixture.get();

        var criteria = ReservationSeatLookupCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
        when(reader.existsOne(criteria)).thenReturn(false);

        var result = checker.check(INITIAL_USER_ID, locator, range);

        assertTrue(result.reservable());
        assertNull(result.rejectReason());
        verify(reader).existsOne(criteria);
    }
}
