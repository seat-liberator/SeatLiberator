package com.seatliberator.seatliberator.reservation.application.booking.contract.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreator;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.criteria.ReservationSeatOverlapCriteria;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.ReservationTestSupport.reservationCreatorCommand;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationCreator 테스트")
public class ReservationCreatorTest {

    @Mock
    ReservationReader reader;

    @Mock
    ReservationStore store;

    ReservationCreator creator;

    @BeforeEach
    void run() {
        creator = new DefaultReservationCreator(reader, store);
    }

    @Test
    @DisplayName("같은 locator, range에 예약 존재 시 예외")
    void throw_exception_when_reservation_already_exists_in_locator_and_range() {
        var command = reservationCreatorCommand();

        when(reader.existsOverlapping(any(ReservationSeatOverlapCriteria.class))).thenReturn(true);

        assertThatApplicationThrownBy(() -> creator.create(command))
                .hasErrorCode(ReservationApplicationErrorCode.RESERVATION_TIME_CONFLICT);

        verify(store, never()).save(any());
    }

    @Test
    @DisplayName("command 값으로 Reservation 생성")
    void create_reservation_from_command() {
        var command = reservationCreatorCommand();

        when(store.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = creator.create(command);

        assertThat(result.getLocator().isSame(command.locator())).isTrue();
        assertThat(result.getRange().isSame(command.range())).isTrue();

        verify(store).save(any());
    }
}