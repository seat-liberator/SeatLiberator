package com.seatliberator.seatliberator.verification.application.port.in;

import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationLocator;
import com.seatliberator.seatliberator.reservation.application.port.in.entry.ReservationEntry;
import com.seatliberator.seatliberator.verification.application.port.in.command.Requester;

public interface ReservationVerifier {
    ReservationEntry verify(ReservationLocator reservationLocator, Requester requester);
}
