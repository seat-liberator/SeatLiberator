package com.seatliberator.seatliberator.reservation.persistence;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;

public class AbstractPersistenceAdapterTest {
    protected Clock clock;
    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void run() {
        clock = TestClock.getFixed();
    }

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
