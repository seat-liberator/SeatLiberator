package com.seatliberator.seatliberator.idempotency.core;

import com.seatliberator.seatliberator.idempotency.core.decision.DefaultExecutionDecisionEngine;
import com.seatliberator.seatliberator.idempotency.core.decision.ExecutionDecisionEngine;
import com.seatliberator.seatliberator.idempotency.core.factory.*;
import com.seatliberator.seatliberator.idempotency.core.processor.DefaultIdempotentProcessor;
import com.seatliberator.seatliberator.idempotency.core.processor.IdempotentProcessor;
import com.seatliberator.seatliberator.idempotency.core.store.IdempotencyStore;
import com.seatliberator.seatliberator.idempotency.core.store.IdempotencyStoreConfigurationProperties;
import com.seatliberator.seatliberator.idempotency.core.store.InMemoryIdempotencyStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.resilience.annotation.EnableResilientMethods;

import java.time.Clock;

@AutoConfiguration
@EnableConfigurationProperties({
        IdempotencyStoreConfigurationProperties.class,
        IdempotencyFactoryConfigurationProperties.class
})
@EnableResilientMethods
public class IdempotencyCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IdempotentProcessor.class)
    IdempotentProcessor idempotentProcessor(
            IdempotencyStore idempotencyStore,
            ExecutionDecisionEngine executionDecisionEngine
    ) {
        return new DefaultIdempotentProcessor(idempotencyStore, executionDecisionEngine);
    }

    @Bean
    @ConditionalOnMissingBean(IdempotencyStore.class)
    IdempotencyStore idempotencyStore(Clock clock) {
        return new InMemoryIdempotencyStore(clock);
    }

    @Bean
    @ConditionalOnMissingBean(ExecutionDecisionEngine.class)
    ExecutionDecisionEngine executionDecisionEngine(
            IdempotencyStoreConfigurationProperties properties,
            Clock clock
    ) {
        return new DefaultExecutionDecisionEngine(properties.maxAttemptCount(), properties.timeout(), clock);
    }

    @Bean
    @ConditionalOnMissingBean(IdempotencyContextFactory.class)
    IdempotencyContextFactory<?> idempotencyContextFactory() {
        return new ImmutableIdempotencyContextFactory();
    }

    @Bean
    @ConditionalOnMissingBean(IdempotencyKeyFactory.class)
    IdempotencyKeyFactory<?> idempotencyKeyFactory() {
        return new ImmutableIdempotencyKeyFactory();
    }

    @Bean
    @ConditionalOnMissingBean(IdempotencyKeyComposer.class)
    IdempotencyKeyComposer idempotencyKeyComposer(
            IdempotencyFactoryConfigurationProperties properties
    ) {
        return new SeparatorBasedIdempotencyKeyComposer(properties.keySeparator());
    }

    @Bean
    @ConditionalOnMissingBean(Clock.class)
    Clock clock() {
        return Clock.systemUTC();
    }
}
