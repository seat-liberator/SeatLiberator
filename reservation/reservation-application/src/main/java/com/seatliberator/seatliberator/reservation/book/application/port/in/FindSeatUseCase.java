package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.result.SeatResult;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;

import java.util.List;

public interface FindSeatUseCase {
    SeatResult read(SeatLocator locator);

    List<SeatResult> findAllByRoomId(String roomId);
}
