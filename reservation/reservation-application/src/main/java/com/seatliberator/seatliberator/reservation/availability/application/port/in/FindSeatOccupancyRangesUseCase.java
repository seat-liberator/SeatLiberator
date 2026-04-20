package com.seatliberator.seatliberator.reservation.availability.application.port.in;

import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindOccupancyRangesQuery;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.result.SeatOccupancyRangeResult;

import java.util.List;

public interface FindSeatOccupancyRangesUseCase {
    List<SeatOccupancyRangeResult> find(FindOccupancyRangesQuery query);
}
