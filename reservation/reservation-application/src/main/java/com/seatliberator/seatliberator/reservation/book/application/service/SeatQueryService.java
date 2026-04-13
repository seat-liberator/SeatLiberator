package com.seatliberator.seatliberator.reservation.book.application.service;

import com.seatliberator.seatliberator.reservation.book.application.port.in.FindSeatUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.result.SeatResult;
import com.seatliberator.seatliberator.reservation.book.application.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatQueryService implements FindSeatUseCase {
    private final SeatReader query;

    @Override
    public SeatResult read(SeatLocator locator) {
        return query.findByLocator(locator)
                .map(SeatResult::from)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND));
    }

    @Override
    public List<SeatResult> findAllByRoomId(String roomId) {
        return query.findByRoomId(roomId).stream()
                .map(SeatResult::from)
                .toList();
    }
}
