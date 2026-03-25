package com.seatliberator.seatliberator.vacancy.application.service;

import com.seatliberator.seatliberator.vacancy.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.vacancy.application.exception.ApplicationException;
import com.seatliberator.seatliberator.vacancy.application.port.in.VacancyAlertRequester;
import com.seatliberator.seatliberator.vacancy.application.port.in.command.VacancyAlertRequestCommand;
import com.seatliberator.seatliberator.vacancy.application.port.out.VacancyAlertRequestReader;
import com.seatliberator.seatliberator.vacancy.application.port.out.VacancyAlertRequestStore;
import com.seatliberator.seatliberator.vacancy.domain.VacancyAlertRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultVacancyAlertRequester implements VacancyAlertRequester {
    private final VacancyAlertRequestReader reader;
    private final VacancyAlertRequestStore store;

    @Override
    public VacancyAlertRequest request(VacancyAlertRequestCommand command) {
        if (reader.existsActiveRequestFor(
                command.userId(),
                command.roomId(),
                command.seatId(),
                command.startTime(),
                command.endTime()
        )) {
            throw new ApplicationException(ApplicationErrorCode.DUPLICATED_REQUEST);
        }

        var request = VacancyAlertRequest.of(
                command.userId(),
                command.roomId(),
                command.seatId(),
                command.startTime(),
                command.endTime(),
                command.requestedAt()
        );

        return store.save(request);
    }
}
