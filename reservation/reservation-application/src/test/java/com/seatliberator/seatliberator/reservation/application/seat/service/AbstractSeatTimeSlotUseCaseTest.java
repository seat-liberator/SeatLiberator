package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractSeatTimeSlotUseCaseTest<T> {
    @Mock
    SeatReader seatReader;

    @Mock
    SeatTimeSlotReader seatTimeSlotReader;

    @Mock
    SeatTimeSlotStore seatTimeSlotStore;

    Clock clock;

    T useCase;

    abstract T init(SeatReader seatReader, SeatTimeSlotReader seatTimeSlotReader, SeatTimeSlotStore seatTimeSlotStore, Clock clock);

    @BeforeEach
    void run() {
        clock = fixedClock;
        useCase = init(seatReader, seatTimeSlotReader, seatTimeSlotStore, clock);
    }
}
