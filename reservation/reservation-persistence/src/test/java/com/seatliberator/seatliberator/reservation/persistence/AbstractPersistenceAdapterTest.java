package com.seatliberator.seatliberator.reservation.persistence;

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
        clock = TestSupport.fixedClock;
    }

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
