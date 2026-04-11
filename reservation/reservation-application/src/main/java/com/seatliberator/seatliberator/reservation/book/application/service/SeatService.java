package com.seatliberator.seatliberator.reservation.book.application.service;

import com.seatliberator.seatliberator.reservation.book.application.port.in.SeatManager;
import com.seatliberator.seatliberator.reservation.book.application.port.in.SeatReader;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.SeatCreateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.SeatUpdateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.SeatEntry;
import com.seatliberator.seatliberator.reservation.book.application.port.out.SeatQuery;
import com.seatliberator.seatliberator.reservation.book.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.SeatExclusion;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatService implements SeatManager, SeatReader {

    private final SeatStore store;
    private final SeatQuery query;

    @Override
    public boolean create(SeatCreateCommand command) {
        var locator = SimpleSeatLocator.from(command.roomId(), command.seatId());

        var conflict = query.existsByLocator(locator);
        if (conflict) return false;

        Seat seat = Seat.create(
                command.roomId(),
                command.seatId()
        );

        store.save(seat);

        return true;
    }

    @Override
    public boolean update(SeatUpdateCommand command) {
        var oldLocator = SimpleSeatLocator.from(command.oldRoomId(), command.oldSeatId());
        var oldSeat = query.findByLocator(oldLocator)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND));

        var exclude = SeatExclusion.of(List.of(oldSeat.getId()));
        var conflict = query.existsByLocator(oldLocator, exclude);
        if (conflict) return false;

        oldSeat.update(command.newRoomId(), command.newSeatId());

        return true;
    }

    @Override
    public boolean delete(String roomId, String seatId) {
        var locator = SimpleSeatLocator.from(roomId, seatId);
        store.deleteByLocator(locator);

        return true;
    }

    @Override
    public SeatEntry read(SeatLocator locator) {
        return query.findByLocator(locator)
                .map(SeatEntry::from)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND));
    }

    @Override
    public List<SeatEntry> findAllByRoomId(String roomId) {
        return query.findByRoomId(roomId).stream()
                .map(SeatEntry::from)
                .toList();
    }
}
