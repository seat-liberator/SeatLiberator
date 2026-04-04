package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;
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
        var userId = "user1";
        var locator = SimpleSeatLocator.from("room1", "seat1");
        var range1 = SimpleTimeRange.from(
                now.plusSeconds(60),
                now.plusSeconds(120)
        );
        var range2 = SimpleTimeRange.from(
                now.plusSeconds(90),
                now.plusSeconds(150)
        );

        var r1 = VacancyAlertRequest.create(userId, locator, range1, now);

        var r2 = VacancyAlertRequest.create(userId, locator, range2, now);

        // when
        store.save(r1);
        store.save(r2);

        // then
        boolean exists1 = reader.existsByUserIdAndLocatorAndRangeAndStatus(userId, locator, range1, VacancyAlertStatus.ACTIVE);
        boolean exists2 = reader.existsByUserIdAndLocatorAndRangeAndStatus(userId, locator, range2, VacancyAlertStatus.ACTIVE);

        assertThat(exists1).isTrue();
        assertThat(exists2).isTrue();
    }

    private VacancyAlertRequest create(Instant now) {
        var locator = SimpleSeatLocator.from("room1", "seat1");
        var range = SimpleTimeRange.from(now.plusSeconds(60), now.plusSeconds(120));
        return VacancyAlertRequest.create("user1", locator, range, now);
    }
}
