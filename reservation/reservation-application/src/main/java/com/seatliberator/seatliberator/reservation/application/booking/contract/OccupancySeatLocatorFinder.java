package com.seatliberator.seatliberator.reservation.application.booking.contract;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;

import java.util.List;

public interface OccupancySeatLocatorFinder {
    List<SeatLocator> find(String roomId, TimeRange range);
}