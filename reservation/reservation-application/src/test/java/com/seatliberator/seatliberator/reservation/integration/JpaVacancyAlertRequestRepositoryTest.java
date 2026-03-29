package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestReader;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import com.seatliberator.seatliberator.reservation.vacancy.domain.VacancyAlertRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Jpa Vacancy Alert Request Repository")
public class JpaVacancyAlertRequestRepositoryTest {

    @Autowired
    VacancyAlertRequestStore store;

    @Autowired
    VacancyAlertRequestReader reader;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("동일 요청 저장 시 예외 발생한다")
    void 동일_요청_저장_시_예외_발생한다() {

        // given
        var r1 = create();
        var r2 = create();

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
    void 시간_다르면_저장_성공() {
        // given
        var now = Instant.now();

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
                r1.getTargetStartTime(),
                r1.getTargetEndTime()
        );
        boolean exists2 = reader.existsActiveRequestFor(
                "user1",
                "room1",
                "seat1",
                r2.getTargetStartTime(),
                r2.getTargetEndTime()
        );


        assertThat(exists1).isTrue();
        assertThat(exists2).isTrue();
    }

    private VacancyAlertRequest create() {
        Instant now = Instant.now();

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
