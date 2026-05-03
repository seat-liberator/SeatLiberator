package com.seatliberator.seatliberator.reservation.application.booking.contract;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;

import java.util.List;

public interface OccupancySeatRangeFinder {
    List<TimeRange> find(SeatLocator locator, TimeRange range);
}
