package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestReader;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
@TransactionalReservationIntegrationTest
@DisplayName("Integration: Jpa Vacancy Alert Request Repository")
public class JpaVacancyAlertRequestRepositoryTest {
    private static final Instant BASE_TIME = Instant.parse("2026-01-01T00:00:00Z");

    @Autowired
    VacancyAlertRequestStore store;

    @Autowired
    VacancyAlertRequestReader reader;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("동일 요청 저장 시 예외 발생한다")
    void throw_exception_when_saving_duplicate_request() {

        // given
        var r1 = create(BASE_TIME);
        var r2 = create(BASE_TIME);

        // when
        store.save(r1);

        // then
        assertThatThrownBy(() -> {
            store.save(r2);
            em.flush();
        });
    }

    @Test
    @DisplayName("시간이 다르면 동일 좌석도 저장된다")
    void save_requests_when_request_times_differ() {
        // given
        var now = BASE_TIME;

        var r1 = VacancyAlertRequest.of(
                "user1",
                "room1",
                "seat1",
                now.plusSeconds(60),
                now.plusSeconds(120),
                now
        );

        var r2 = VacancyAlertRequest.of(
                "user1",
                "room1",
                "seat1",
                now.plusSeconds(180),
                now.plusSeconds(240),
                now
        );

        // when
        store.save(r1);
        store.save(r2);

        // then
        boolean exists1 = reader.existsActiveRequestFor(
                "user1",
                "room1",
                "seat1",
                r1.getRange().startAt(),
                r1.getRange().endAt()
        );
        boolean exists2 = reader.existsActiveRequestFor(
                "user1",
                "room1",
                "seat1",
                r2.getRange().startAt(),
                r2.getRange().endAt()
        );


        assertThat(exists1).isTrue();
        assertThat(exists2).isTrue();
    }

    private VacancyAlertRequest create(Instant now) {
        return VacancyAlertRequest.of(
                "user1",
                "room1",
                "seat1",
                now.plusSeconds(60),
                now.plusSeconds(120),
                now
        );
    }
}
