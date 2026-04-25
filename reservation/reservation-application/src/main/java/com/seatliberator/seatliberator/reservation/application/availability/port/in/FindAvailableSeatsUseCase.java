package com.seatliberator.seatliberator.reservation.application.availability.port.in;

import com.seatliberator.seatliberator.reservation.application.availability.port.in.query.FindAvailableSeatQuery;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.result.AvailableSeatResult;

import java.util.List;

public interface FindAvailableSeatsUseCase {
    List<AvailableSeatResult> find(FindAvailableSeatQuery query);
}
