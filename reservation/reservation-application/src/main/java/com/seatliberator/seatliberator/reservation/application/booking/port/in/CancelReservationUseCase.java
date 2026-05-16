package com.seatliberator.seatliberator.reservation.application.booking.port.in;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CancelReservationCommand;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;

public interface CancelReservationUseCase {
    ReservationResult cancel(CancelReservationCommand command);
}
