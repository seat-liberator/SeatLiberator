package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreateAuthorizedPolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreatePolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreator;
import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatePolicyCommand;
import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatorCommand;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CancelReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CreateReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.UpdateReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CancelReservationCommand;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.UpdateReservationCommand;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationSeatOverlapCriteria;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleInstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class ReservationCommandService implements
        CreateReservationUseCase,
        UpdateReservationUseCase,
        CancelReservationUseCase {
    private final ReservationStore reservationStore;
    private final ReservationReader reader;
    private final SeatStore seatStore;

    private final ReservationCreateAuthorizedPolicy createAuthorizedPolicy;
    private final ReservationCreatePolicy createPolicy;
    private final ReservationCreator creator;

    private final Clock clock;

    @Override
    @Transactional
    public ReservationResult create(CreateReservationCommand command) {
        seatStore.findForUpdate(command.locator())
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND));

        reader.findByUserId(command.userId()).ifPresent(e -> {
            throw new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_ALREADY_EXISTS);
        });

        var authorizedPolicyResult = createAuthorizedPolicy.evaluate(command.requester());
        if (authorizedPolicyResult.rejected())
            throw new ReservationApplicationPolicyException(authorizedPolicyResult.reason());

        var createPolicyResult = createPolicy.evaluate(ReservationCreatePolicyCommand.from(command));
        if (createPolicyResult.rejected())
            throw new ReservationApplicationPolicyException(createPolicyResult.reason());

        var created = creator.create(ReservationCreatorCommand.from(command));

        return ReservationResult.of(created);
    }

    @Transactional
    @Override
    public ReservationResult update(UpdateReservationCommand command) {
        Reservation reservation = reader.findByUserId(command.userId()).orElseThrow();
        var previousLocator = reservation.getLocator();

        lockSeats(
                previousLocator.roomId(),
                previousLocator.seatId(),
                command.roomId(),
                command.seatId()
        );

        var currentLocator = SimpleSeatLocator.of(command.roomId(), command.seatId());
        var currentRange = SimpleInstantRange.of(command.startTime(), command.endTime());

        var criteria = ReservationSeatOverlapCriteria.of(currentLocator, currentRange)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED, ReservationStatus.USED));
        var conflict = reader.existsOverlapping(criteria);

        if (conflict)
            throw new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_TIME_CONFLICT);

        reservation.update(command.userId(), command.roomId(), command.seatId(), command.startTime(), command.endTime());

        return ReservationResult.of(reservation);
    }

    @Transactional
    @Override
    public ReservationResult cancel(CancelReservationCommand command) {
        Reservation reservation = reader.findByUserId(command.userId())
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));

        var locator = reservation.getLocator();
        seatStore.findForUpdate(
                locator.roomId(),
                locator.seatId()
        ).ifPresent(seat -> {
        });

        var now = clock.instant();
        reservation.cancel(now);

        var saved = reservationStore.save(reservation);

        return ReservationResult.of(saved);
    }

    private void lockSeats(String roomId1, String seatId1, String roomId2, String seatId2) {

        if (roomId1.equals(roomId2) && seatId1.equals(seatId2)) {
            seatStore.findForUpdate(roomId1, seatId1).orElseThrow();
        }

        int roomCompare = roomId1.compareTo(roomId2);

        if (roomCompare < 0 || (roomCompare == 0 && seatId1.compareTo(seatId2) < 0)) {
            seatStore.findForUpdate(roomId1, seatId1).orElseThrow();
            seatStore.findForUpdate(roomId2, seatId2).orElseThrow();
        } else {
            seatStore.findForUpdate(roomId2, seatId2).orElseThrow();
            seatStore.findForUpdate(roomId1, seatId1).orElseThrow();
        }
    }
}
