package com.seatliberator.seatliberator.reservation.persistence.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
        classes = {
                com.seatliberator.seatliberator.reservation.persistence.ReservationPersistenceTestConfiguration.class,
                ReservationPersistenceIntegrationTestConfiguration.class
        },
        properties = "seatliberator.resource-server.security.authorize.enabled=false"
)
@ActiveProfiles("test")
public @interface ReservationPersistenceIntegrationTest {
}
