package com.seatliberator.seatliberator.reservation.vacancy.application.service;

import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.RequestVacancyAlertUseCase;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCreateCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class VacancyAlertService implements RequestVacancyAlertUseCase {
    private final VacancyAlertRequestStore store;
    private final ReservationReader reader;

    private final Clock clock;

    @Override
    public VacancyAlertRequest request(VacancyAlertRequestCreateCommand command) {
        var locator = SimpleSeatLocator.from(command.roomId(), command.seatId());
        var range = SimpleTimeRange.from(command.startTime(), command.endTime());

        var criteria = ReservationOverlapCriteria.of(locator, range)
                .withStatuses(ReservationStatus.RESERVED);
        var reservationExists = reader.existsOverlapping(criteria);
        if (!reservationExists) {
            throw new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND);
        }

        var exists = store.existsByUserIdAndLocatorAndRangeAndStatus(command.userId(), locator, range, VacancyAlertRequestStatus.ACTIVE);

        if (exists) throw new ReservationApplicationException(ReservationApplicationErrorCode.DUPLICATED_REQUEST);

        var now = clock.instant();

        var request = VacancyAlertRequest.create(command.userId(), locator, range, command.behavior(), now);

        try {
            return store.save(request);
        } catch (DataIntegrityViolationException e) {
            throw new ReservationApplicationException(ReservationApplicationErrorCode.DUPLICATED_REQUEST);
        }
    }

    @Override
    public void cancel(VacancyAlertRequestCancelCommand command) {
        var now = clock.instant();

        var waitlist = store.findById(command.alertId())
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.NOT_FOUND));

        if (!waitlist.getUserId().equals(command.userId())) {
            throw new ReservationApplicationException(ReservationApplicationErrorCode.UNAUTHORIZED_CANCELLATION);
        }

        waitlist.cancel(now);
        store.save(waitlist);
    }

}
