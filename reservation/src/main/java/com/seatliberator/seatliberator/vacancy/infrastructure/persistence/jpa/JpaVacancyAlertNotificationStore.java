package com.seatliberator.seatliberator.vacancy.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.vacancy.application.port.out.VacancyAlertNotificationQuery;
import com.seatliberator.seatliberator.vacancy.application.port.out.VacancyAlertNotificationStore;
import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertNotification;
import com.seatliberator.seatliberator.vacancy.infrastructure.persistence.jpa.repository.VacancyAlertNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaVacancyAlertNotificationStore implements
        VacancyAlertNotificationStore,
        VacancyAlertNotificationQuery {
    private final VacancyAlertNotificationRepository repository;

    @Override
    public List<VacancyAlertNotification> saveAll(List<VacancyAlertNotification> notifications) {
        return repository.saveAll(notifications);
    }

    @Override
    public List<VacancyAlertNotification> findAllByUserIdOrderByNotifiedAtDesc(String userId) {
        return repository.findAllByUserIdOrderByNotifiedAtDesc(userId);
    }
}
