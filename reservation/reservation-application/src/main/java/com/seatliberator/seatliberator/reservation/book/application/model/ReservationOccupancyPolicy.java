package com.seatliberator.seatliberator.reservation.book.application.model;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;

public class ReservationOccupancyPolicy {
    public boolean isOccupied(ReservationStatus status) {
        return switch (status) {
            case RESERVED, USED -> true;
            case CANCELED, EXPIRED -> false;
        };
    }
}
