package com.seatliberator.seatliberator.reservation.vacancy.application.service;

import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertStatus;
import com.seatliberator.seatliberator.reservation.shared.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.shared.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.vacancy.application.exception.VacancyApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.vacancy.application.exception.VacancyApplicationException;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.VacancyAlertRequester;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestReader;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class DefaultVacancyAlertRequester implements VacancyAlertRequester {
    private final VacancyAlertRequestReader reader;
    private final VacancyAlertRequestStore store;

    private final Clock clock;

    @Override
    public VacancyAlertRequest request(VacancyAlertRequestCommand command) {
        var locator = SimpleSeatLocator.from(command.roomId(), command.seatId());
        var range = SimpleTimeRange.from(command.startTime(), command.endTime());

        var exists = reader.existsByUserIdAndLocatorAndRangeAndStatus(command.userId(), locator, range, VacancyAlertStatus.ACTIVE);

        if (exists) throw new VacancyApplicationException(VacancyApplicationErrorCode.DUPLICATED_REQUEST);

        var now = clock.instant();

        var request = VacancyAlertRequest.create(command.userId(), locator, range, now);

        try {
            return store.save(request);
        } catch (DataIntegrityViolationException e) {
            throw new VacancyApplicationException(VacancyApplicationErrorCode.DUPLICATED_REQUEST);
        }
    }

    @Override
    public void cancelVacancyAlert(VacancyAlertCancelCommand command) {
        var now = clock.instant();

        VacancyAlertRequest alert = reader.findById(command.alertId())
                .orElseThrow(() -> new VacancyApplicationException(VacancyApplicationErrorCode.NOT_FOUND));

        alert.cancel(command.userId(), now);

        store.save(alert);
    }

}
