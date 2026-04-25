package com.seatliberator.seatliberator.reservation.persistence;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication(scanBasePackages = "com.seatliberator.seatliberator.reservation")
@EntityScan(basePackages = "com.seatliberator.seatliberator.reservation")
public class ReservationPersistenceTestConfiguration {
}
