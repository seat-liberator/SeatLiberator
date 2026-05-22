package com.seatliberator.seatliberator.identity.server.persistence;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackages = "com.seatliberator.seatliberator.identity.server.domain")
@EnableJpaRepositories(basePackages = "com.seatliberator.seatliberator.identity.server.persistence")
public class JpaPersistenceAdapterTestConfig {
}
