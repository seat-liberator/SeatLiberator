package com.seatliberator.seatliberator.eventrelay.jpa;

import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEvent;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackageClasses = JpaStoredEvent.class)
public class TestApplication {
}
