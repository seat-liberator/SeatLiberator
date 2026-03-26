package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.EventRelayCoreAutoConfiguration;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStore;
import com.seatliberator.seatliberator.eventrelay.core.store.EventStoreConfigurationProperties;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

@AutoConfiguration
@AutoConfigureAfter(DataJpaRepositoriesAutoConfiguration.class)
@AutoConfigureBefore(EventRelayCoreAutoConfiguration.class)
@ConditionalOnClass({EntityManager.class, JpaRepository.class})
@EnableConfigurationProperties(EventStoreConfigurationProperties.class)
public class EventRelayJpaSupportAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EventStore.class)
    EventStore eventStore(
            JpaStoredEventRepository jpaStoredEventRepository,
            EventAcceptor eventAcceptor,
            EventStoreConfigurationProperties properties
    ) {
        return new JpaEventStore(jpaStoredEventRepository, eventAcceptor, properties.batchSize());
    }

    @Bean
    @Qualifier("postgres")
    @ConditionalOnMissingBean(EventAcceptor.class)
    EventAcceptor eventAcceptor(EntityManager entityManager) {
        return new JpaPostgresqlEventAcceptor(entityManager);
    }
}
