package com.seatliberator.seatliberator.reservation.vacancy.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface VacancyAlertRequestRepository extends JpaRepository<VacancyAlertRequest, UUID>, JpaSpecificationExecutor<VacancyAlertRequest> {
}
