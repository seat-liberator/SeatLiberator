package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreateAuthorizedPolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreatePolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreator;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractReservationCommandServiceTest {
    @Mock
    ReservationStore store;

    @Mock
    ReservationReader reader;

    @Mock
    SeatStore seatStore;

    @Mock
    ReservationCreateAuthorizedPolicy createAuthorizedPolicy;

    @Mock
    ReservationCreatePolicy createPolicy;

    @Mock
    ReservationCreator creator;

    ReservationCommandService service;

    @BeforeEach
    void setUp() {
        service = new ReservationCommandService(store, reader, seatStore, createAuthorizedPolicy, createPolicy, creator, fixedClock);
    }
}
