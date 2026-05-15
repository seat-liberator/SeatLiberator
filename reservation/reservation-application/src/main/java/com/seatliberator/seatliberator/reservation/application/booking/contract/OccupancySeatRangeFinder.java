package com.seatliberator.seatliberator.reservation.application.booking.contract;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;

import java.util.List;

public interface OccupancySeatRangeFinder {
    List<InstantRange> find(SeatLocator locator, InstantRange range);
}
