package com.seatliberator.seatliberator.reservation.application.booking.port.in;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindAvailableSlotsBySeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FindAvailableSlotsBySeatUseCase {
    Map<LocalDate, List<SeatTimeSlotResult>> findAtDateRange(FindAvailableSlotsBySeatQuery query);
}