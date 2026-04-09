package com.seatliberator.seatliberator.reservation.vacancy.application.port.in;

import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCreateCommand;

public interface VacancyAlertRequester {
    VacancyAlertRequest request(VacancyAlertRequestCreateCommand command);

    void cancel(VacancyAlertRequestCancelCommand command);
}
