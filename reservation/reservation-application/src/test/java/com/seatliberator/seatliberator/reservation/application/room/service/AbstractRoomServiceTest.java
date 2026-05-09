package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractRoomServiceTest<T> {
    @Mock
    RoomReader reader;

    @Mock
    RoomStore store;

    Clock clock;

    Instant now;

    T useCase;

    abstract T init(RoomReader reader, RoomStore store, Clock clock);

    @BeforeEach
    void run() {
        clock = fixedClock;
        now = clock.instant();
        useCase = init(reader, store, clock);
    }
}
