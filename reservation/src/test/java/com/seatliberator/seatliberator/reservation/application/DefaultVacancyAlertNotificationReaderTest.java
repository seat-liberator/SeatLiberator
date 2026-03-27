package com.seatliberator.seatliberator.reservation.application;

import com.seatliberator.seatliberator.vacancy.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.vacancy.application.exception.ApplicationException;
import com.seatliberator.seatliberator.vacancy.application.port.out.VacancyAlertNotificationQuery;
import com.seatliberator.seatliberator.vacancy.application.service.DefaultVacancyAlertNotificationReader;
import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultVacancyAlertNotificationReaderTest {

    @Test
    @DisplayName("Read own notifications")
    void readOwnNotifications() {
        var query = mock(VacancyAlertNotificationQuery.class);
        var service = new DefaultVacancyAlertNotificationReader(query);
        var now = Instant.now();
        var notification = VacancyAlertNotification.of(
                UUID.randomUUID(),
                "user-1",
                "room-1",
                "seat-1",
                now.plusSeconds(10),
                now.plusSeconds(20),
                now
        );

        when(query.findAllByUserIdOrderByNotifiedAtDesc("user-1")).thenReturn(List.of(notification));

        var entries = service.readAllByUser("user-1", "user-1");

        assertThat(entries).hasSize(1);
        assertThat(entries.getFirst().seatId()).isEqualTo("seat-1");
    }

    @Test
    @DisplayName("Deny when actor tries to read another user notifications")
    void denyAccessForAnotherUser() {
        var query = mock(VacancyAlertNotificationQuery.class);
        var service = new DefaultVacancyAlertNotificationReader(query);

        assertThatThrownBy(() -> service.readAllByUser("user-1", "user-2"))
                .isInstanceOf(ApplicationException.class)
                .extracting(ex -> ((ApplicationException) ex).getErrorCode())
                .isEqualTo(ApplicationErrorCode.NOTIFICATION_ACCESS_DENIED);
    }
}
