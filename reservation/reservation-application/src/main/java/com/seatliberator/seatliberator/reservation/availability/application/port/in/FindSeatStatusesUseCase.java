package com.seatliberator.seatliberator.reservation.availability.application.port.in;

import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindSeatStatusesQuery;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.result.SeatStatusesResult;

import java.util.List;

public interface FindSeatStatusesUseCase {
    List<SeatStatusesResult> find(FindSeatStatusesQuery query);
}
