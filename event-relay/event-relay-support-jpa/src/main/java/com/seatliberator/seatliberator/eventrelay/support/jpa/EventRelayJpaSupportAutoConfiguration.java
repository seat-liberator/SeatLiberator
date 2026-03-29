package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.EventRelayCoreAutoConfiguration;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStoreConfigurationProperties;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigureBefore(EventRelayCoreAutoConfiguration.class)
@ConditionalOnClass(EntityManager.class)
@EnableConfigurationProperties(EventStoreConfigurationProperties.class)
public class EventRelayJpaSupportAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(EventStore.class)
    JpaEventStore eventStore(
            JpaStoredEventPersistencePort jpaStoredEventPersistencePort,
            EventAcceptor eventAcceptor,
            EventStoreConfigurationProperties properties
    ) {
        return new JpaEventStore(jpaStoredEventPersistencePort, eventAcceptor, properties.batchSize());
    }

    @Bean
    @ConditionalOnMissingBean(JpaStoredEventPersistencePort.class)
    JpaStoredEventPersistence jpaStoredEventPersistence(EntityManager entityManager) {
        return new JpaStoredEventPersistence(entityManager);
    }

    @Bean
    @Qualifier("postgres")
    @ConditionalOnMissingBean(EventAcceptor.class)
    JpaPostgresqlEventAcceptor eventAcceptor(EntityManager entityManager) {
        return new JpaPostgresqlEventAcceptor(entityManager);
    }
}
