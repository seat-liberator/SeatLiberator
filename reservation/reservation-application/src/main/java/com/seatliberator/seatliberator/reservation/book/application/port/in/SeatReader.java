package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.SeatEntry;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;

import java.util.List;

public interface SeatReader {
    SeatEntry read(SeatLocator locator);

    List<SeatEntry> findAllByRoomId(String roomId);
}
