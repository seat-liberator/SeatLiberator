package com.seatliberator.seatliberator.reservation.vacancy.application.port.in;

import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCommand;

public interface VacancyAlertRequester {
    VacancyAlertRequest request(VacancyAlertRequestCommand command);

    void cancelVacancyAlert(VacancyAlertCancelCommand command);
}
