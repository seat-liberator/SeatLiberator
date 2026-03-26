package com.seatliberator.seatliberator.eventrelay.jpa;

import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEvent;
import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEventRepository;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses = JpaStoredEventRepository.class)
@EntityScan(basePackageClasses = JpaStoredEvent.class)
public class TestApplication {
}
