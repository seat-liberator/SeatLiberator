package com.seatliberator.seatliberator.reservation.vacancy.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface VacancyAlertRequestRepository extends JpaRepository<VacancyAlertRequest, UUID>, JpaSpecificationExecutor<VacancyAlertRequest> {
}
