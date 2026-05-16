package com.seatliberator.seatliberator.reservation.application.booking.contract.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreator;
import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatorCommand;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.criteria.ReservationSeatOverlapCriteria;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DefaultReservationCreator implements ReservationCreator {
    private final ReservationReader reader;
    private final ReservationStore store;

    @Override
    @Transactional
    public Reservation create(ReservationCreatorCommand command) {
        var criteria = ReservationSeatOverlapCriteria.of(command.locator(), command.range())
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
        var exists = reader.existsOverlapping(criteria);

        if (exists)
            throw new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_TIME_CONFLICT);

        var reservation = Reservation.of(
                command.userId(),
                command.locator(),
                command.range(),
                ReservationStatus.RESERVED
        );

        store.save(reservation);

        return reservation;
    }
}
