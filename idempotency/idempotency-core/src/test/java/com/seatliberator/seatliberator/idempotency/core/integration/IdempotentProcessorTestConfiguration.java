package com.seatliberator.seatliberator.idempotency.core.integration;

import com.seatliberator.seatliberator.idempotency.core.decision.DefaultExecutionDecisionEngine;
import com.seatliberator.seatliberator.idempotency.core.decision.ExecutionDecisionEngine;
import com.seatliberator.seatliberator.idempotency.core.processor.DefaultIdempotentProcessor;
import com.seatliberator.seatliberator.idempotency.core.processor.IdempotentProcessor;
import com.seatliberator.seatliberator.idempotency.core.store.IdempotencyStore;
import com.seatliberator.seatliberator.idempotency.core.store.InMemoryIdempotencyStore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.resilience.annotation.EnableResilientMethods;

import java.time.Clock;
import java.time.Duration;

@TestConfiguration
@EnableResilientMethods(proxyTargetClass = true)
public class IdempotentProcessorTestConfiguration {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    IdempotencyStore idempotencyStore(Clock clock) {
        return new InMemoryIdempotencyStore(clock);
    }

    @Bean
    ExecutionDecisionEngine executionDecisionEngine(Clock clock) {
        return new DefaultExecutionDecisionEngine(
                3,
                Duration.ofSeconds(30),
                clock
        );
    }

    @Bean
    IdempotentProcessor idempotentProcessor(
            IdempotencyStore idempotencyStore,
            ExecutionDecisionEngine executionDecisionEngine
    ) {
        return new DefaultIdempotentProcessor(
                idempotencyStore,
                executionDecisionEngine
        );
    }
}
