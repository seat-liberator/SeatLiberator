package com.seatliberator.seatliberator.eventrelay.jpa;

import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaPostgresqlEventAcceptor;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEventPersistence;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    JpaStoredEventPersistence jpaStoredEventPersistence(EntityManager entityManager) {
        return new JpaStoredEventPersistence(entityManager);
    }

    @Bean
    JpaPostgresqlEventAcceptor acceptor(EntityManager entityManager) {
        return new JpaPostgresqlEventAcceptor(entityManager);
    }
}
