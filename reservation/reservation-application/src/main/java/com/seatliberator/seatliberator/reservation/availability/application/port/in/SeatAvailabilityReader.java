package com.seatliberator.seatliberator.reservation.availability.application.port.in;

import com.seatliberator.seatliberator.reservation.availability.application.port.in.entry.AvailabilitySeatEntry;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.util.List;

public interface SeatAvailabilityReader {
    List<AvailabilitySeatEntry> findAvailabilitySeats(String roomId, TimeRange range);
}
