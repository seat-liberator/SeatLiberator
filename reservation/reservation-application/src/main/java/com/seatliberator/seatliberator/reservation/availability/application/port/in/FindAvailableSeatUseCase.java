package com.seatliberator.seatliberator.reservation.availability.application.port.in;

import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindAvailableSeatQuery;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.result.AvailableSeatResult;

import java.util.List;

public interface FindAvailableSeatUseCase {
    List<AvailableSeatResult> findAvailabilitySeats(FindAvailableSeatQuery query);
}
