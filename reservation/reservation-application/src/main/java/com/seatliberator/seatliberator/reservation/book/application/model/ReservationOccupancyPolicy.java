package com.seatliberator.seatliberator.reservation.book.application.model;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;

import java.util.EnumSet;
import java.util.Set;

public class ReservationOccupancyPolicy {
    public boolean isOccupied(ReservationStatus status) {
        return occupyingStatuses().contains(status);
    }

    public Set<ReservationStatus> occupyingStatuses() {
        return EnumSet.of(ReservationStatus.RESERVED, ReservationStatus.USED);
    }
}
