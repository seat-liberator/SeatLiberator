package com.seatliberator.seatliberator.reservation.persistence.integration;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ReservationPersistenceIntegrationTest
@Transactional
public @interface TransactionalReservationPersistenceIntegrationTest {
}
