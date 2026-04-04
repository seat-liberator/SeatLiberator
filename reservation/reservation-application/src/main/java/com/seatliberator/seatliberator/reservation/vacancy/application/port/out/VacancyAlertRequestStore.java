package com.seatliberator.seatliberator.reservation.vacancy.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;

public interface VacancyAlertRequestStore {
    VacancyAlertRequest save(VacancyAlertRequest vacancyAlertRequest);
}
