package com.seatliberator.seatliberator.reservation.verification.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationEntry;
import com.seatliberator.seatliberator.reservation.verification.application.port.in.command.Requester;

public interface ReservationPolicyReader {
    ReservationEntry read(ReservationLocator reservationLocator, Requester requester);
}
