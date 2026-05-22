package com.seatliberator.seatliberator.identity.server.persistence;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractPersistenceAdapterTest {
    @Autowired
    EntityManager entityManager;

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
