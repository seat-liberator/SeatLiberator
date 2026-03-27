package com.seatliberator.seatliberator.vacancy.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VacancyAlertNotificationRepository extends JpaRepository<VacancyAlertNotification, UUID> {
    List<VacancyAlertNotification> findAllByUserIdOrderByNotifiedAtDesc(String userId);
}
