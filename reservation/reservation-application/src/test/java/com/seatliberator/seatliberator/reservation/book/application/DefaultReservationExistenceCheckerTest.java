package com.seatliberator.seatliberator.reservation.book.application;

import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.book.application.contract.service.DefaultReservationExistenceChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Default Reservation Existence Checker")
public class DefaultReservationExistenceCheckerTest {
    @Mock
    ReservationStore store;

    @InjectMocks
    DefaultReservationExistenceChecker checker;

    @Test
    @DisplayName("같은 좌석과 시간대의 예약이 존재하면 true를 반환한다")
    void return_true_when_reservation_exists_in_locator_and_range() {
        var locator = createLocator();
        var range = createRange();

        when(store.existsByLocatorAndRange(locator, range)).thenReturn(true);

        assertTrue(checker.isExistsByLocatorAndRange(locator, range));
        verify(store).existsByLocatorAndRange(locator, range);
    }

    @Test
    @DisplayName("같은 좌석과 시간대의 예약이 존재하지 않으면 false를 반환한다")
    void return_false_when_reservation_does_not_exist_in_locator_and_range() {
        var locator = createLocator();
        var range = createRange();

        when(store.existsByLocatorAndRange(locator, range)).thenReturn(false);

        assertFalse(checker.isExistsByLocatorAndRange(locator, range));
        verify(store).existsByLocatorAndRange(locator, range);
    }
}
