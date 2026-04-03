package com.seatliberator.seatliberator.eventrelay.support.jpa.autoconfigure;

import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import com.seatliberator.seatliberator.eventrelay.support.jpa.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigurationPackage(basePackageClasses = JpaStoredEvent.class)
@AutoConfigureBefore(HibernateJpaAutoConfiguration.class)
@ConditionalOnClass(EntityManager.class)
@ConditionalOnProperty(
        prefix = "event-relay",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@ConditionalOnProperty(
        prefix = "event-relay.jpa",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class EventRelayJpaAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(EventStore.class)
    JpaEventStore eventStore(
            JpaStoredEventPersistencePort jpaStoredEventPersistencePort,
            EventAcceptor eventAcceptor
    ) {
        return new JpaEventStore(jpaStoredEventPersistencePort, eventAcceptor);
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
