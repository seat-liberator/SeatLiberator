package com.seatliberator.seatliberator.reservation.vacancy.application.service;

import com.seatliberator.seatliberator.reservation.vacancy.application.exception.VacancyApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.vacancy.application.exception.VacancyApplicationException;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.VacancyAlertRequester;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestReader;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import com.seatliberator.seatliberator.reservation.vacancy.domain.VacancyAlertRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
            throw new VacancyApplicationException(VacancyApplicationErrorCode.DUPLICATED_REQUEST);
        }

        var request = VacancyAlertRequest.of(
                command.userId(),
                command.roomId(),
                command.seatId(),
                command.startTime(),
                command.endTime(),
                command.requestedAt()
        );

        try {
            return store.save(request);
        } catch (DataIntegrityViolationException e) {
            throw new VacancyApplicationException(VacancyApplicationErrorCode.DUPLICATED_REQUEST);
        }
    }

    @Override
    public void cancelVacancyAlert(VacancyAlertCancelCommand command) {

        VacancyAlertRequest alert = reader.findById(command.alertId())
                .orElseThrow(() -> new VacancyApplicationException(VacancyApplicationErrorCode.NOT_FOUND));

        alert.cancel(command.userId(), Instant.now());

        store.save(alert);
    }

}
