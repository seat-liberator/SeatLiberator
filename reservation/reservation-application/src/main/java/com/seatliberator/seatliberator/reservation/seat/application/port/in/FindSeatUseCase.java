package com.seatliberator.seatliberator.reservation.seat.application.port.in;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.result.SeatResult;

import java.util.List;

public interface FindSeatUseCase {
    SeatResult read(SeatLocator locator);

    List<SeatResult> findAllByRoomId(String roomId);
}
