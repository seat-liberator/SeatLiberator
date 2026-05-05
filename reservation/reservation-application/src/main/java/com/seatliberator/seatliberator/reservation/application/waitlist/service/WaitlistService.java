package com.seatliberator.seatliberator.reservation.application.waitlist.service;

import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationSeatOverlapCriteria;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.CancelWaitlistUseCase;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.CreateWaitlistUseCase;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command.CancelWaitlistCommand;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command.CreateWaitlistCommand;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistStore;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleInstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class WaitlistService implements
        CreateWaitlistUseCase,
        CancelWaitlistUseCase {
    private final WaitlistStore store;
    private final ReservationReader reader;

    private final Clock clock;

    @Override
    public Waitlist create(CreateWaitlistCommand command) {
        var locator = SimpleSeatLocator.of(command.roomId(), command.seatId());
        var range = SimpleInstantRange.of(command.startTime(), command.endTime());

        var criteria = ReservationSeatOverlapCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
        var reservationExists = reader.existsOverlapping(criteria);
        if (!reservationExists) {
            throw new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND);
        }

        var exists = store.existsByUserIdAndLocatorAndRangeAndStatus(command.userId(), locator, range, WaitlistStatus.ACTIVE);

        if (exists) throw new ReservationApplicationException(ReservationApplicationErrorCode.DUPLICATED_REQUEST);

        var now = clock.instant();

        var request = Waitlist.create(command.userId(), locator, range, command.behavior(), now);

        try {
            return store.save(request);
        } catch (DataIntegrityViolationException e) {
            throw new ReservationApplicationException(ReservationApplicationErrorCode.DUPLICATED_REQUEST);
        }
    }

    @Override
    public void cancel(CancelWaitlistCommand command) {
        var now = clock.instant();

        var waitlist = store.findById(command.waitlistId())
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.NOT_FOUND));

        if (!waitlist.getUserId().equals(command.userId())) {
            throw new ReservationApplicationException(ReservationApplicationErrorCode.UNAUTHORIZED_CANCELLATION);
        }

        waitlist.cancel(now);
        store.save(waitlist);
    }

}
