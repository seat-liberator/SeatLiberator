package com.seatliberator.seatliberator.reservation.book.application.service;

import com.seatliberator.seatliberator.reservation.book.application.exception.BookApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.book.application.exception.BookApplicationException;
import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationManager;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationUpdateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationEntry;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.book.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.domain.Reservation;
import com.seatliberator.seatliberator.reservation.shared.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.shared.domain.SimpleTimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService implements ReservationManager {
    private final ReservationStore reservationStore;
    private final SeatStore seatStore;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final Clock clock;

    @Override
    public ReservationEntry create(ReservationCreateCommand command) {
        seatStore.findForUpdate(command.roomId(), command.seatId())
                .orElseThrow(() -> new BookApplicationException(BookApplicationErrorCode.SEAT_NOT_FOUND));

        reservationStore.findByUserId(command.userId()).ifPresent(e -> {
            throw new BookApplicationException(BookApplicationErrorCode.RESERVATION_ALREADY_EXISTS);
        });

        var locator = SimpleSeatLocator.from(command.roomId(), command.seatId());
        var range = SimpleTimeRange.from(command.startTime(), command.endTime());

        var conflict = reservationStore.existsByLocatorAndRange(locator, range);

        if (conflict) throw new BookApplicationException(BookApplicationErrorCode.RESERVATION_TIME_CONFLICT);

        var reservation = Reservation.create(
                command.userId(),
                command.roomId(),
                command.seatId(),
                command.startTime(),
                command.endTime()
        );

        var saved = reservationStore.save(reservation);

        return ReservationEntry.of(saved);
    }

    @Transactional
    @Override
    public ReservationEntry update(ReservationUpdateCommand command) {
        Reservation reservation = reservationStore.findByUserId(command.userId()).orElseThrow();
        var previousLocator = reservation.getLocator();

        lockSeats(
                previousLocator.roomId(),
                previousLocator.seatId(),
                command.roomId(),
                command.seatId()
        );

        var currentLocator = SimpleSeatLocator.from(command.roomId(), command.seatId());
        var currentRange = SimpleTimeRange.from(command.startTime(), command.endTime());

        var conflict = reservationStore.existsByLocatorAndRangeWithExcludeIds(
                currentLocator,
                currentRange,
                List.of(reservation.getId())
        );

        if (conflict) throw new BookApplicationException(BookApplicationErrorCode.RESERVATION_TIME_CONFLICT);

        reservation.update(command.userId(), command.roomId(), command.seatId(), command.startTime(), command.endTime());

        return ReservationEntry.of(reservation);
    }

    @Transactional
    @Override
    public ReservationEntry cancel(String userId) {
        Reservation reservation = reservationStore.findByUserId(userId)
                .orElseThrow(() -> new BookApplicationException(BookApplicationErrorCode.RESERVATION_NOT_FOUND));

        var locator = reservation.getLocator();
        seatStore.findForUpdate(
                locator.roomId(),
                locator.seatId()
        ).ifPresent(seat -> {
        });

        var now = clock.instant();
        reservation.cancel(now);

        var saved = reservationStore.save(reservation);

        return ReservationEntry.of(saved);
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
