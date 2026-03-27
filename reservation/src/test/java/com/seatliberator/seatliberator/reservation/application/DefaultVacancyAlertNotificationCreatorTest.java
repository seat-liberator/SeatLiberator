package com.seatliberator.seatliberator.reservation.application;

import com.seatliberator.seatliberator.vacancy.application.port.in.command.VacancyAlertNotificationCreateCommand;
import com.seatliberator.seatliberator.vacancy.application.port.out.VacancyAlertNotificationStore;
import com.seatliberator.seatliberator.vacancy.application.service.DefaultVacancyAlertNotificationCreator;
import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultVacancyAlertNotificationCreatorTest {

    @Test
    @DisplayName("Create notifications and persist mapped entities")
    @SuppressWarnings("unchecked")
    void createNotifications() {
        var store = mock(VacancyAlertNotificationStore.class);
        var service = new DefaultVacancyAlertNotificationCreator(store);
        var now = Instant.now();

        var command = new VacancyAlertNotificationCreateCommand(
                UUID.randomUUID(),
                "user-1",
                "room-1",
                "seat-1",
                now.plusSeconds(10),
                now.plusSeconds(20),
                now
        );

        when(store.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        var entries = service.createAll(List.of(command));

        var captor = ArgumentCaptor.forClass(List.class);
        verify(store).saveAll(captor.capture());
        var saved = (List<VacancyAlertNotification>) captor.getValue();

        assertThat(saved).hasSize(1);
        assertThat(saved.getFirst().getUserId()).isEqualTo("user-1");
        assertThat(saved.getFirst().getSeatId()).isEqualTo("seat-1");
        assertThat(entries).hasSize(1);
        assertThat(entries.getFirst().userId()).isEqualTo("user-1");
    }
}
