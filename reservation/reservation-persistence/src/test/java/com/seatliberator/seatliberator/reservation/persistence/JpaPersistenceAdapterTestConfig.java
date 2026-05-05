package com.seatliberator.seatliberator.reservation.persistence;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackages = "com.seatliberator.seatliberator.reservation.domain")
@EnableJpaRepositories(basePackages = "com.seatliberator.seatliberator.reservation.persistence")
public class JpaPersistenceAdapterTestConfig {
}
