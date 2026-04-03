package com.seatliberator.seatliberator.eventrelay.consumer;

import com.seatliberator.seatliberator.eventrelay.support.jpa.JpaStoredEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest(classes = EventRelayJpaAutoConfigurationTest.ConsumerApplication.class)
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=com.seatliberator.seatliberator.eventrelay.core.EventRelayCoreAutoConfiguration",
        "spring.datasource.url=jdbc:tc:postgresql:18-alpine:///event-relay-auto-config-test",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EventRelayJpaAutoConfigurationTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("컨슈머와 서포트 모듈의 엔티티를 모두 관리 대상으로 포함한다")
    void registers_consumer_and_support_entities() {
        assertThatCode(() -> entityManagerFactory.getMetamodel().entity(ConsumerEntity.class))
                .doesNotThrowAnyException();
        assertThatCode(() -> entityManagerFactory.getMetamodel().entity(JpaStoredEvent.class))
                .doesNotThrowAnyException();
    }

    @SpringBootApplication
    static class ConsumerApplication {
    }

    @Entity
    static class ConsumerEntity {

        @Id
        private Long id;
    }
}
