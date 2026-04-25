package com.seatliberator.seatliberator.reservation.application.availability.model;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SeatLocatorKey;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SeatReservationStatusClassifier {
    private final Map<SeatLocatorKey, SeatReservationStatus> statuses;

    private SeatReservationStatusClassifier(Map<SeatLocatorKey, SeatReservationStatus> statuses) {
        this.statuses = statuses;
    }

    public static SeatReservationStatusClassifier from(
            Collection<? extends SeatLocator> seatLocators,
            Collection<? extends SeatLocator> occupiedLocators
    ) {
        if (seatLocators == null) throw new IllegalArgumentException("seatLocators must not be null.");
        if (occupiedLocators == null) throw new IllegalArgumentException("occupiedLocators must not be null.");

        var occupiedKeys = occupiedLocators.stream().map(SeatLocator::key).collect(Collectors.toSet());

        var statuses = new LinkedHashMap<SeatLocatorKey, SeatReservationStatus>();

        for (var locator : seatLocators) {
            var key = locator.key();
            var status = occupiedKeys.contains(key)
                    ? SeatReservationStatus.OCCUPIED
                    : SeatReservationStatus.AVAILABLE;

            statuses.put(key, status);
        }

        return new SeatReservationStatusClassifier(statuses);
    }

    public Map<SeatLocatorKey, SeatReservationStatus> toMap() {
        return Map.copyOf(statuses);
    }
}