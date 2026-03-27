package com.seatliberator.seatliberator.vacancy.application.port.in;

import com.seatliberator.seatliberator.vacancy.application.port.in.command.VacancyAlertCancelCommand;
import com.seatliberator.seatliberator.vacancy.application.port.in.command.VacancyAlertRequestCommand;
import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertRequest;

public interface VacancyAlertRequester {
    VacancyAlertRequest request(VacancyAlertRequestCommand command);

    void cancelVacancyAlert(VacancyAlertCancelCommand command);
}
