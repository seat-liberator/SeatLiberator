package com.seatliberator.seatliberator.reservation.application.availability.port.in;

import com.seatliberator.seatliberator.reservation.application.availability.port.in.query.FindSeatStatusesQuery;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.result.SeatStatusesResult;

import java.util.List;

public interface FindSeatStatusesUseCase {
    List<SeatStatusesResult> find(FindSeatStatusesQuery query);
}
