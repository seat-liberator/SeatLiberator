package com.seatliberator.seatliberator.reservation.application.availability.port.in;

import com.seatliberator.seatliberator.reservation.application.availability.port.in.query.FindOccupancyRangesQuery;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.result.SeatOccupancyRangeResult;

import java.util.List;

public interface FindSeatOccupancyRangesUseCase {
    List<SeatOccupancyRangeResult> find(FindOccupancyRangesQuery query);
}
