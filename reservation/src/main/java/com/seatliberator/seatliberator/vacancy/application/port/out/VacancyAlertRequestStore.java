package com.seatliberator.seatliberator.vacancy.application.port.out;

import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertRequest;

public interface VacancyAlertRequestStore {
    VacancyAlertRequest save(VacancyAlertRequest vacancyAlertRequest);
}
